const express = require('express');
const router = express.Router();
const user_handle = require('../router_handle/user');


router.get('/pageList',user_handle.pageList)
router.get('/pageDetails',user_handle.pageDetails)
router.get('/destroy',user_handle.destroy)
router.get('/create',user_handle.create)
router.post('/addData',user_handle.addData)
router.get('/getVersion',user_handle.getVersion)
router.get('/checkin',user_handle.checkin)
router.get('/checksuccess',user_handle.checksuccess)
router.get('/pay',user_handle.pay)
module.exports = router;