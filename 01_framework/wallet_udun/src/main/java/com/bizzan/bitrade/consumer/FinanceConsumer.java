package com.bizzan.bitrade.consumer;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bizzan.bitrade.constant.BooleanEnum;
import com.bizzan.bitrade.constant.WithdrawStatus;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.Member;
import com.bizzan.bitrade.entity.WithdrawRecord;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.MemberService;
import com.bizzan.bitrade.service.MemberWalletService;
import com.bizzan.bitrade.service.WithdrawRecordService;
import com.bizzan.bitrade.util.MessageResult;

import com.spark.bipay.constant.CoinType;
import com.spark.bipay.http.ResponseMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Calendar;

import com.bizzan.bitrade.service.BiPayService;

@Component
public class FinanceConsumer {
    private Logger logger = LoggerFactory.getLogger(FinanceConsumer.class);
    @Autowired
    private CoinService coinService;

    @Autowired
    private MemberWalletService walletService;

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WithdrawRecordService withdrawRecordService;

    @Autowired
    private BiPayService biPayService;

    /**
     * 处理充值消息，key值为币种的名称（注意是全称，如Bitcoin）
     *
     * @param record
     */
    @KafkaListener(topics = {"deposit"})
    public void handleDeposit(ConsumerRecord<String, String> record) {
        logger.info("topic={},key={},value={}", record.topic(), record.key(), record.value());
        if (StringUtils.isEmpty(record.value())) {
            return;
        }
        JSONObject json = JSON.parseObject(record.value());
        if (json == null) {
            return;
        }
        BigDecimal amount = json.getBigDecimal("amount");
        String txid = json.getString("txid");
        String address = json.getString("address");
        Coin coin = coinService.findOne(record.key());
        
        logger.info("coin={}", coin);
        
        if(coin.getAccountType() == 1) {
        	Long memoId = json.getLong("userId"); // 备注Memo
        	Long userId = memoId - 345678; // 注意，此处与前端memo必须保持一致
        	Member m = memberService.findOne(userId);
        	if(m != null && walletService.findDepositByTxid(txid) == null) {
        		MessageResult mr = walletService.recharge2(userId, coin, address, amount, txid);
	            logger.info("wallet recharge result:{}", mr);
        	}
        }else {
	        if (coin != null && walletService.findDeposit(address, txid) == null) {
	            MessageResult mr = walletService.recharge(coin, address, amount, txid);
	            logger.info("wallet recharge result:{}", mr);
	        }
        }
    }

    /**
     * 处理提币请求,调用U盾，自动转账
     *
     * @param record
     */
    @KafkaListener(topics = {"withdraw"})
    public void handleWithdraw(ConsumerRecord<String, String> record) {
        logger.info("topic={},key={},value={}", record.topic(), record.key(), record.value());
        if (StringUtils.isEmpty(record.value())) {
            return;
        }
        JSONObject json = JSON.parseObject(record.value());
        Long withdrawId = json.getLong("withdrawId");
        try {
            // udun发起提币
            logger.info("发起Udun提币：" + withdrawId);
            WithdrawRecord withdrawRecord=withdrawRecordService.findOne(withdrawId);
            CoinType coinType = CoinType.unitOf(withdrawRecord.getCoin().getUnit());
            biPayService.transfer(String.valueOf(withdrawId), withdrawRecord.getArrivedAmount(), coinType, coinType.getCode(), withdrawRecord.getAddress(), "");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("auto withdraw failed,error={}", e.getMessage());
            logger.info("== Udun自动转账失败 ===");
            withdrawRecordService.autoWithdrawFail(withdrawId);
        }
    }

    /**
     * 异步打钱后返回状态
     * @param record
     */
    @KafkaListener(topics = {"withdraw-notify"})
    public void withdrawNotify(ConsumerRecord<String, String> record){
        logger.info("topic={},accessKey={},value={}", record.topic(), record.key(), record.value());
        if (StringUtils.isEmpty(record.value())) {
            return;
        }
        JSONObject json = JSON.parseObject(record.value());
        Long withdrawId = json.getLong("withdrawId");
        WithdrawRecord withdrawRecord=withdrawRecordService.findOne(withdrawId);
        if(withdrawRecord==null){
            return;
        }
        String txid=json.getString("txid");
        int status=json.getInteger("status");
        //转账失败，状态变回等待放币
        if(status==0){
            withdrawRecord.setStatus(WithdrawStatus.WAITING);
            withdrawRecordService.save(withdrawRecord);
        }else if(status==1){
            withdrawRecordService.withdrawSuccess(withdrawId, txid);
        }
    }
}
