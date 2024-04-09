const mysql = require('mysql2')

const db = mysql.createPool({
    host: 'wqby6666.mysql.rds.aliyuncs.com',
    user: 'wangquanbaye',
    password: 'BAye6666',
    database: 'leaveimitate'
})

module.exports = db;