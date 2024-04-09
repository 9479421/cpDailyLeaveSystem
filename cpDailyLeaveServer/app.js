const express = require('express');
const app = express();
const cors = require('cors');
const path = require('path')
const userRouter = require('./router/user');


app.use(cors());
app.use(express.urlencoded({ extended: false }))
app.use('/',userRouter);
app.set('views', path.join(__dirname, 'views'));
app.set("view engine","ejs");//模版引擎设置为 ejs
app.use(express.static(path.join(__dirname,'public')))

app.listen(8888,()=>{
    console.log('服务器已启动，在8888端口运行');
})