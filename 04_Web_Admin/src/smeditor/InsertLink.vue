<template>
  <div class="insert-link">
    <div class="insert-link-box">
      <span class="cancel" @click='cancelClick'>X</span>
      <p>插入链接</p>
      <input type="text" name="text" placeholder="链接文本" v-model='text' v-on:keyup.enter='conformClick'>
      <input type="text" name="link" placeholder="链接地址" v-model='link'>
      <button @click='conformClick'> 确认</button>
    </div>
  </div>
</template>
<script type="text/javascript">
export default {
  name: 'InsertLink',
  data () {
    return {
      link: '',
      text: ''
    }
  },
  props: ['insertLink', 'cancel', 'propText'],
  methods: {
    conformClick () {
      this.insertLink(this.link, this.text)
    },
    cancelClick () {
      this.cancel()
    }
  },
  watch: {
    'propText': function () {
      this.text = this.propText
      this.link = this.propLink
    }
  }
}
</script>

<style type="text/css">
.insert-link {
  position: fixed;
  top: 0px;
  left: 0;
  background-color: rgba(255, 255, 255, 1);
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.insert-link-box {
  position: relative;
  padding: 30px 20px;
  border-radius: 6px;
  margin: 0 auto 24px;
  background-clip: padding-box;
  box-shadow: 0 2px 8px rgba(0,0,0,.2);
}
.insert-link p {
  text-align: center;
  padding: 20px 0px;
  font-size: 25px;
}
.insert-link input {
  text-align: left;
  margin: 5px 0px;
  border: 1px solid rgb(200,200,200);
  font-size: 14px;
  padding: 10px 5px;
  width: calc(100% - 20px);
}

.insert-link .cancel {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 2;
  font-size: 20px;
  width: 40px;
  height: 40px;
  text-align: center;
  line-height: 40px;
  cursor: pointer;
}
</style>
