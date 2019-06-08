package cn.edu.neu.shop.pin.service;

import cn.edu.neu.shop.pin.exception.CheckInFailedException;
import cn.edu.neu.shop.pin.mapper.PinSettingsConstantMapper;
import cn.edu.neu.shop.pin.mapper.PinUserCreditRecordMapper;
import cn.edu.neu.shop.pin.mapper.PinUserMapper;
import cn.edu.neu.shop.pin.model.PinUser;
import cn.edu.neu.shop.pin.model.PinUserCreditRecord;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class UserCreditRecordService {

    @Autowired
    private PinUserCreditRecordMapper pinUserCreditRecordMapper;

    @Autowired
    private PinUserMapper pinUserMapper;

    @Autowired
    private PinSettingsConstantMapper pinSettingsConstantMapper;

    /**
     * 按照需求规约中格式，获取用户签到记录
     * @param userId
     * @return
     */
    public JSONObject getUserCreditData(Integer userId) {
        JSONObject data = new JSONObject();
        PinUser user = pinUserMapper.selectByPrimaryKey(userId);
        Integer credit = user.getCredit();
        data.put("credit", credit);
        Integer totalCheckInDays = pinUserCreditRecordMapper.getCheckInDaysNum(userId);
        data.put("totalCheckInDays", totalCheckInDays);
        Integer continuousCheckInDays = getContinuousCheckInDaysNum(userId);
        data.put("continuousCheckInDays", continuousCheckInDays);
        List<PinUserCreditRecord> list = pinUserCreditRecordMapper.getUserCreditRecordByDateDesc(userId);
        data.put("creditHistory", list);
        return data;
    }

    /**
     * 每日签到
     * @param userId
     * @throws Exception
     */
    public void dailyCheckIn(Integer userId) throws Exception {
        List<PinUserCreditRecord> list = pinUserCreditRecordMapper.getCheckInDaysInfo(userId);
        Integer credit = getCredit();
        Integer increment = getIncrement();
        Integer limit = getLimit();
        PinUserCreditRecord record;
        if(list.size() == 0) { // 暂无签到记录
            record = new PinUserCreditRecord(userId, credit, PinUserCreditRecord.TYPE_FROM_CHECK_IN, new Date(), 1);
            pinUserMapper.updateUserCredit(userId, credit);
            pinUserCreditRecordMapper.insert(record);
        }
        else if(hasCheckedIn(userId)) { // 今日已签到
            throw new CheckInFailedException("签到失败！请勿重复签到！");
        }
        else {
            Date yesterday = this.getYesterday(new Date());
            Integer note = getContinuousCheckInDaysNum(userId);
            Integer creditVal = (credit+note*increment>limit) ? limit : credit+note*increment;
            record = new PinUserCreditRecord(userId, creditVal, PinUserCreditRecord.TYPE_FROM_CHECK_IN, new Date(), note);
            pinUserCreditRecordMapper.insert(record);
        }
    }

    /**
     * 判断今天是否签到了
     * @param userId
     * @return
     */
    public Boolean hasCheckedIn(Integer userId) {
        List<PinUserCreditRecord> list = pinUserCreditRecordMapper.getCheckInDaysInfo(userId);
        if(list.size() == 0) {
            return false;
        }
        else {
            PinUserCreditRecord p = list.get(0);
            Date date = p.getCreateTime();
            return isTheSameDay(date, new Date());
        }
    }

    /**
     * 获取某一用户的连续签到天数（用到了DP思想）
     * @param userId
     * @return
     */
    public Integer getContinuousCheckInDaysNum(Integer userId) {
        List<PinUserCreditRecord> list = pinUserCreditRecordMapper.getCheckInDaysInfo(userId);
        if(list.size() == 0) {
            return 0;
        }
        else {
            PinUserCreditRecord p = list.get(0);
            Date date = p.getCreateTime(), yesterday = this.getYesterday(new Date());
            Integer note = isTheSameDay(date, yesterday) ? p.getNote() : 1;
            return note;
        }
    }

    /**
     * 获取Constant表中的check_in_credit值，为了简化代码
     * @return
     */
    public Integer getCredit() {
        return Integer.parseInt(pinSettingsConstantMapper
                .selectByPrimaryKey(new String("check_in_credit"))
                .getConstantValue());
    }

    /**
     * 获取Constant表中的check_in_credit_increment值，为了简化代码
     * @return
     */
    private Integer getIncrement() {
        return Integer.parseInt(pinSettingsConstantMapper
                .selectByPrimaryKey(new String("check_in_credit_increment"))
                .getConstantValue());
    }

    /**
     * 获取Constant表中的check_in_credit_limit值，为了简化代码
     * @return
     */
    private Integer getLimit() {
        return Integer.parseInt(pinSettingsConstantMapper
                .selectByPrimaryKey(new String("check_in_credit_limit"))
                .getConstantValue());
    }

    /**
     * 判断时间是不是今天
     * @param date
     * @return    是返回true，不是返回false
     */
    private boolean isNow(Date date) {
        Date now = new Date(); // 当前时间
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String nowDay = sf.format(now); // 获取今天的日期
        String day = sf.format(date); // 对比的时间
        return day.equals(nowDay);
    }

    /**
     * 给定今天日期，判断昨天日期
     * @param today
     * @return
     */
    public Date getYesterday(Date today) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        return calendar.getTime();
    }

    /**
     * 判断两个Date类型的日期是否相同
     * @param date1
     * @param date2
     * @return
     */
    public Boolean isTheSameDay(Date date1, Date date2) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        String day1 = sf.format(date1), day2 = sf.format(date2);
        return day1.equals(day2);
    }
}