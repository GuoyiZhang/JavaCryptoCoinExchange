# 交易机器人

- exchange_robot：总工程（父工程）
- er_market：外部行情获取引擎，获取外部价格工程
- er_common：暂时无用
- er_robot_normal：一般机器人（外部行情有价格的交易对）
- er_robot_price：恒定价格机器人（如EUSDT/USDT交易对恒定价格为1:1），与er_robot_normal基本相同，唯一的不同是获取外部价格是从robotParams获取
- er_robot_custom：控盘机器人，可任意控制盘面价格