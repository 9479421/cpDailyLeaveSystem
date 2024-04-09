const { append } = require("express/lib/response");
const db = require("../db/index");

//是否授权，可以改成false，用户就开心死了
var isAuth = false;

exports.pageList = (req, res) => {
  var query = req.query;
  console.log(query.userId);
  if (!query.userId || !query.name) {
    return res.send("不合法的数据");
  }

  db.query(
    "select * from authuser where identity = ? and expireDate > NOW()",
    query.userId,
    (err, result) => {
      if (err) {
        return res.send("查询错误");
      } else {
        if (result.length <= 0 && isAuth) {
          //是否要求授权
          return res.render("pay", {
            sNumber: query.userId,
            sName: query.name,
          });
          // return res.send(
          //   "<h1>学号：" + query.userId + "暂未授权，禁止使用---权q模块<h1>"
          // );
        }
        db.query(
          "select * from leavedata where sNumber = ? order by sStart desc",
          query.userId,
          (err, result) => {
            console.log(result);
            if (err) {
              return res.send("查询错误");
            } else {
              // 计算天数
              for (let i = 0; i < result.length; i++) {
                let days;
                let dates = result[i].sDate.split("至");
                var dateSpan;
                const sDate = new Date(dates[0]).getTime();
                const eDate = new Date(dates[1]).getTime();
                dateSpan = eDate - sDate;
                dateSpan = Math.abs(dateSpan);
                days = Math.floor(dateSpan / (24 * 3600 * 1000)) + 1;
                result[i].sDays = days;
              }
              //传入sNumber

              res.render("pageList", {
                list: result,
                sNumber: query.userId,
                sName: query.name,
              });
            }
          }
        );
      }
    }
  );
};
exports.pageDetails = (req, res) => {
  var query = req.query;

  //解析时间
  var sd = new Date(query.sStart);
  query.sStart = timeFormatSeconds(sd);
  var subHour = Math.floor(Math.random() * 10) + 1;
  var newHour = sd.getHours() - subHour;
  sd.setHours(newHour);
  sd.setMinutes(Math.floor(Math.random() * 60) + 1);
  sd.setSeconds(Math.floor(Math.random() * 60) + 1);
  query.sSend = timeFormatSeconds(sd);

  db.query(
    "select status from leavedata where id = ?",
    query.id,
    (err, result) => {
      console.log(result);
      if (err) {
        return res.send("数据异常");
      } else {
        console.log(result);
        query.status = result[0].status;
        return res.render("pageDetails", { data: query });
      }
    }
  );
};

exports.destroy = (req, res) => {
  var query = req.query;
  db.query(
    "update leavedata set status='已销假' where id = ?",
    query.id,
    (err, result) => {
      console.log(result);
      if (err) {
        return res.send({ code: 201 });
      } else {
        return res.send({ code: 200 });
      }
    }
  );
};
exports.create = (req, res) => {
  var query = req.query;
  if (!query.sNumber || !query.sName) {
    return res.send("不合法的数据");
  }
  var retmsg = query.msg;
  if (!retmsg) {
    retmsg = "";
  }
  res.render("create", {
    sNumber: query.sNumber,
    sName: query.sName,
    msg: retmsg,
  });
};
exports.addData = (req, res) => {
  var body = req.body;
  var type = "";
  var date = "";
  if (body.sType == 0) {
    type = "病假";
  } else if (body.sType == 1) {
    type = "事假（因私）";
  } else {
    type = "事假（因公）";
  }
  date = body.sDate.replace("T", " ") + " 至 " + body.eDate.replace("T", " ");
  var data = [
    body.sNumber,
    body.sName,
    body.sAcademy,
    body.sClass,
    type,
    date,
    new Date(),
    body.sReason,
    body.sPhone,
    body.teacherName,
  ];
  db.query(
    "insert into leavedata (sNumber,sName,sAcademy,sClass,sType,sDate,sStart,sReason,sPhone,teacherName,status) VALUES (?,?,?,?,?,?,?,?,?,?,'已通过')",
    data,
    (err, result) => {
      console.log(err);
      if (err) {
        res.redirect("create?sNumber=" + body.sNumber + "&msg=加入失败&sName="+body.sName);
      } else {
        res.redirect("create?sNumber=" + body.sNumber + "&msg=加入成功&sName="+body.sName);
      }
    }
  );
};

exports.getVersion = (req, res) => {
  res.send("3.0");
};

exports.checkin = (req, res) => {
  res.render("checkin", {
    nowDate: formatDateTimeForHM(new Date()),
    nowDate1: formatDateTimeForHMS(new Date()),
  });
};

exports.checksuccess = (req, res) => {
  res.render("checksuccess", {
    phone: req.query.phone,
    name: req.query.name,
    time: formatDateTimeForHMS(new Date()),
    identity: req.query.identity,
  });
};

exports.pay = (req, res) => {
  res.render("pay", {
    sNumber: req.sNumber,
    sName: req.sName,
  });
};

function formatDateTimeForHM(obj) {
  if (obj == null) {
    return null;
  }
  let date = new Date(obj);
  let y = 1900 + date.getYear();
  let m = "0" + (date.getMonth() + 1);
  let d = "0" + date.getDate();
  let h = "0" + date.getHours();
  let mm = "0" + date.getMinutes();
  let s = date.getSeconds();
  return (
    y +
    "-" +
    m.substring(m.length - 2, m.length) +
    "-" +
    d.substring(d.length - 2, d.length) +
    " " +
    h.substring(h.length - 2, h.length) +
    ":" +
    mm.substring(mm.length - 2, mm.length)
  );
}
function formatDateTimeForHMS(obj) {
  if (obj == null) {
    return null;
  }
  let date = new Date(obj);
  let y = 1900 + date.getYear();
  let m = "0" + (date.getMonth() + 1);
  let d = "0" + date.getDate();
  let h = "0" + date.getHours();
  let mm = "0" + date.getMinutes();
  let s = date.getSeconds();
  if (s < 10) {
    s = "0" + s;
  }
  return (
    y +
    "-" +
    m.substring(m.length - 2, m.length) +
    "-" +
    d.substring(d.length - 2, d.length) +
    " " +
    h.substring(h.length - 2, h.length) +
    ":" +
    mm.substring(mm.length - 2, mm.length) +
    ":" +
    s
  );
}

var timeFormatSeconds = function (time) {
  var time = new Date(time);
  time.setHours(time.getHours()+8);
  time = time.toISOString();
  time = time.replace("T", " ");
  time = time.substr(0, 19);
  return time;
};
