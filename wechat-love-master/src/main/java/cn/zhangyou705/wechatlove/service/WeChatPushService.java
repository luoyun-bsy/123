package cn.zhangyou705.wechatlove.service;

import cn.zhangyou705.wechatlove.util.WeatherUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import cn.zhangyou705.wechatlove.util.LoveWordsUtil;
import cn.zhangyou705.wechatlove.util.DateCompareUtil;

import javax.annotation.Resource;

/**
 * @author ZhangYou
 * @description 微信推送Service
 * @date 2022/8/23
 */
@Service
@Slf4j
public class WeChatPushService {

    @Value("${wechat.appId}")
    private String appId;
    @Value("${wechat.secret}")
    private String secret;
    @Value("${wechat.templateId}")
    private String templateId;

    @Value("${tian.caihongpi.key}")
    private String key;

    @Value("${target.know}")
    private String know;
    @Value("${target.birthday}")
    private String birthday;

    @Resource
    private WeatherUtil weatherUtil;

    public void push(String targetOpenId) {

        WxMpInMemoryConfigStorage wxStorage = new WxMpInMemoryConfigStorage();
        wxStorage.setAppId(appId);
        wxStorage.setSecret(secret);
        WxMpService wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(wxStorage);

        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(targetOpenId)
                .templateId(templateId)
                .build();

        WxMpTemplateMessage buildContextResult = this.buildContext(templateMessage);

        try {
            log.info("待推送的内容为：{}", buildContextResult.toJson());
            String msgId = wxMpService.getTemplateMsgService().sendTemplateMsg(buildContextResult);
            log.info("推送成功：{}", msgId);
        } catch (Exception e) {
            log.error("消息推送失败，error：{}", e.getMessage(), e);
        }
    }

    /**
     * 构建模板内容
     *
     * @param templateMessage
     * @return
     */
    private WxMpTemplateMessage buildContext(WxMpTemplateMessage templateMessage) {
        JSONObject todayWeather = weatherUtil.getTodayWeatherByBaiDu();
        templateMessage.addData(new WxMpTemplateData("name", "小池"));
        templateMessage.addData(new WxMpTemplateData("date", todayWeather.getString("date") + " " + todayWeather.getString("week"), "#00BFFF"));
        JSONObject location = todayWeather.getJSONObject("location");
        String city = location.getString("city");
        templateMessage.addData(new WxMpTemplateData("city", city));
        templateMessage.addData(new WxMpTemplateData("weather", todayWeather.getString("text_day")));
        JSONObject now = todayWeather.getJSONObject("now");
        String temp = now.getString("temp");
        templateMessage.addData(new WxMpTemplateData("now_temperature", temp+"度"));
        templateMessage.addData(new WxMpTemplateData("max_temperature", todayWeather.getString("low") + "度"));
        templateMessage.addData(new WxMpTemplateData("min_temperature", todayWeather.getString("high") + "度"));
        templateMessage.addData(new WxMpTemplateData("textNight", todayWeather.getString("text_night")));
        templateMessage.addData(new WxMpTemplateData("windDirDay", todayWeather.getString("wd_day")));
        templateMessage.addData(new WxMpTemplateData("windDirNight", todayWeather.getString("wd_night")));
        templateMessage.addData(new WxMpTemplateData("windScaleDay", todayWeather.getString("ws_day")));
        int know = DateCompareUtil.getknow(this.know);
        if (know > 0) {
            templateMessage.addData(new WxMpTemplateData("know", know+"天~"));
        }
        int birthday = DateCompareUtil.getbirthday(this.birthday);
        if (birthday > 0) {
            templateMessage.addData(new WxMpTemplateData("birthday", birthday + "天~"));
        } else {
            templateMessage.addData(new WxMpTemplateData("birthday", "亲爱的小池！今天是你的生日呀~"));
        }

//        templateMessage.addData(new WxMpTemplateData("caiHongPi", LoveWordsUtil.getCaiHongPi(key)));
        templateMessage.addData(new WxMpTemplateData("loveWords", LoveWordsUtil.getJinJu() + ""));


        return templateMessage;
    }
}
