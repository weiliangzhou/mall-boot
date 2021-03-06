package com.zwl.serviceimpl;

import com.zwl.dao.mapper.UserAccountMapper;
import com.zwl.dao.mapper.WithdrawFlowMapper;
import com.zwl.dao.mapper.WithdrawMapper;
import com.zwl.model.constant.PayWayType;
import com.zwl.model.exception.BSUtil;
import com.zwl.model.po.User;
import com.zwl.model.po.Withdraw;
import com.zwl.model.po.WithdrawFlow;
import com.zwl.model.wxpay.AdminPayVo;
import com.zwl.service.UserService;
import com.zwl.service.WithdrawService;
import com.zwl.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 二师兄超级帅
 * @Title: WithdrawServiceImpl
 * @ProjectName parent
 * @Description: TODO
 * @date 2018/7/520:51
 */
@SuppressWarnings("all")
@Service
public class WithdrawServiceImpl implements WithdrawService {
    @Autowired
    private WithdrawMapper withdrawMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private WithdrawFlowMapper withdrawFlowMapper;
    @Autowired
    private UserService userService;

    @Override
    public List<Withdraw> getWithdrawList() {
        return withdrawMapper.getWithdrawList();
    }

    @Override
    public List<Withdraw> getWithdrawListByUserId(String userId) {
        return withdrawMapper.getWithdrawListByUserId(userId);
    }

    @Override
    public int updateByWithdrawId(String partner_trade_no, String partner_trade_no1, String payment_no) {
        return withdrawMapper.updateByWithdrawId(partner_trade_no, partner_trade_no1, payment_no);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveWithdraw(Integer status, String operator, String withdrawId, String realIp) {
        int count = withdrawMapper.updateWithdrawById(status, operator, withdrawId);
        if (count == 0) {
            BSUtil.isTrue(false, "操作异常,请重新操作");
        }
        //记录流水表
        WithdrawFlow withdrawFlow = new WithdrawFlow();
        withdrawFlow.setWithdrawId(withdrawId);
//        0未提交 1 审核中 2审核通过(前端显示到款中) 3未通过 4成功
        withdrawFlow.setStatus(status);
        withdrawFlow.setOperator(operator);
        withdrawFlowMapper.insertSelective(withdrawFlow);
        Withdraw withdraw = withdrawMapper.getByWithdrawId(withdrawId);
        String openId = withdraw.getOpenId();
        String realName = withdraw.getRealName();
        Integer amount = withdraw.getMoney();
        AdminPayVo adminPayVo = new AdminPayVo();
        adminPayVo.setRealIp(realIp);
        adminPayVo.setOpenId(openId);
        adminPayVo.setRealName(realName);
        adminPayVo.setAmount(amount);
        adminPayVo.setDesc("企业付款");
        adminPayVo.setWithdrawNo(withdrawId);
        //如果审核通过，则调用企业付款,2.0版本
//        wxPayService.adminPay(adminPayVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void apply(Withdraw withdraw) {
        verfiy(withdraw);
        Integer money = withdraw.getMoney() * 100;
        if (money == 0) {
            BSUtil.isTrue(false, "提现金额不能为0");
        }

        String userId = withdraw.getUserId();
        String merchantId = withdraw.getMerchantId();
        Integer balance = userAccountMapper.getBalanceByUserId(userId);
        if (balance == null) {
            BSUtil.isTrue(false, "可用余额不足");
        }
        if (money > balance) {
            BSUtil.isTrue(false, "可用余额不足");
        }
        User user = userService.getByUserId(userId);
        //        需校验该用户是否实名，未实名则返回
//        UserCertification userCertification = userCertificationService.getOneByUserId(userId);
//        0未提交1审核中 2审核通过 3未通过
//        Integer status = userCertification == null ? 0 : userCertification.getStatus();
//        if (status != 2)
//            BSUtil.isTrue(false, "未实名");
        if (StringUtils.isBlank(user.getRealName())) {
            BSUtil.isTrue(false, "未实名");
        }
//        默认写死微信，后期可配置，申请提现金额，可提现金额，点击确认，
//调用微信支付    记录发送参数日志
//        发送支付信息成功
//        发送支付信息失败
//        同时记录提现流水表
//        Integer currentAmount = balance - money;// Arith.sub(balance, money);
        int difBalance = userAccountMapper.subBanlanceByUserId(userId, money);
        if (difBalance == 0) {
            BSUtil.isTrue(false, "更新余额异常");
        }
        withdraw.setWithdrawId(UUIDUtil.getUUID32());
        withdraw.setOpenId(user.getWechatOpenid());
        withdraw.setRealName(user.getRealName());
        withdraw.setMoney(money);
        withdraw.setBalance(balance - money);
        //status设置为1 审核中
        withdraw.setStatus(1);
        withdraw.setMerchantId(merchantId);
        withdrawMapper.insertSelective(withdraw);
        WithdrawFlow withdrawFlow = new WithdrawFlow();
        withdrawFlow.setWithdrawId(withdraw.getWithdrawId());
        withdrawFlow.setStatus(1);
        withdrawFlow.setMerchantId(merchantId);
        withdrawFlowMapper.insertSelective(withdrawFlow);
    }

    private void verfiy(Withdraw withdraw) {
        if (null == withdraw) {
            BSUtil.isTrue(false, "无效参数");
        }
        if (null == withdraw.getMoney() || withdraw.getMoney() < 0) {
            BSUtil.isTrue(false, "无效金额");
        }
        PayWayType payWayType = PayWayType.getPayWayTypeByCode(withdraw.getPayWay());
        if (null == payWayType) {
            BSUtil.isTrue(false, "无效提现类型");
        }
        if (StringUtils.isBlank(withdraw.getAccount())) {
            BSUtil.isTrue(false, "请输入要提现的账号");
        }
        switch (payWayType) {
            case WECHAT:
//                if (StringUtils.isBlank(withdraw.getOpenId())) {
//                    BSUtil.isTrue(false, "OPENID不能为空");
//                }
                break;
            case BALANCE:
                break;
            case BANKCARD:
                if (StringUtils.isBlank(withdraw.getBankProvince())) {
                    BSUtil.isTrue(false, "银行卡所属地址缺失(省)");
                }
                if (StringUtils.isBlank(withdraw.getBankCity())) {
                    BSUtil.isTrue(false, "银行卡所属地址缺失(市)");
                }
                if (StringUtils.isBlank(withdraw.getBankArea())) {
                    BSUtil.isTrue(false, "银行卡所属地址缺失(区)");
                }
//                if (StringUtils.isBlank(withdraw.getBankBranch())) {
//                    BSUtil.isTrue(false, "银行卡支行不能为空");
//                }
                break;
        }
    }
    @Override
    public Integer getTotalMoneyByUserId(String userId) {
        return withdrawMapper.getTotalMoneyByUserId(userId);
    }
    @Override
    public Integer getMoneyByUserIdAndStatus(String userId, Integer status) {
        return withdrawMapper.getMoneyByUserIdAndStatus(userId, status);
    }
}
