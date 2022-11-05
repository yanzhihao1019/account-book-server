package com.rainbow.message.handlers;

import com.rainbow.exceptions.MessageFormatException;
import com.rainbow.message.CommonMessageHandler;
import com.rainbow.message.MessageHandler;
import com.rainbow.message.enums.Type;
import com.rainbow.wx.MessageParam;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class IncreaseHandler extends CommonMessageHandler implements MessageHandler {

    public String pattern = "^[0-9]{1,9}(.{1}[0-9]{1,2})?$";

    private final String[] strategyArray = new String[]{"工资"};

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean checkPattern(String data) {
        return regex(data, pattern);
    }

    @Override
    public String[] getStrategyArray() {
        return this.strategyArray;
    }

    @Override
    public String handler(MessageParam param) {
        if (StringUtils.hasText(param.getData()) && checkPattern(param.getData())) {
            Type type = Type.get(param.getStrategy());
            jdbcTemplate.update("" +
                            "INSERT INTO user_account_log \n" +
                            "       (userid, amount, memo, type, create_by, create_time, update_by, update_time,deleted)\n" +
                            "VALUES (?, ?, ?, ?, ?,DEFAULT, ?, DEFAULT, DEFAULT)",
                    param.getUserid(), Double.parseDouble(param.getData()), null, param.getUserid(), type.getCode(), param.getUserid());
        } else {
            throw new MessageFormatException("数据格式为数字");
        }

        return "操作成功";
    }
}