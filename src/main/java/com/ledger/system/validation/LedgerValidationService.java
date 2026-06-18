package com.ledger.system.validation;

import com.ledger.system.entity.Account;
import com.ledger.system.enums.ErrorCode;
import com.ledger.system.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LedgerValidationService {

    public void validatePayment(
            BigDecimal amount,
            BigDecimal fee,
            Long userAccountId,
            Long merchantAccountId
    ) {

        validateAmount(amount);
        validateFee(amount, fee);
        validateAccounts(userAccountId, merchantAccountId);
        //validateAccountStatus(user, merchant);
    }

    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Invalid Amount", ErrorCode.INVALID_AMOUNT
            );
        }
    }
    public void validateFee(BigDecimal amount, BigDecimal fee) {

        if (fee == null) {
            throw new BusinessException("Fee cannot be null", ErrorCode.INVALID_FEE);
        }

        if (fee.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Fee cannot be negative", ErrorCode.INVALID_FEE
            );
        }

        if (fee.compareTo(amount) >= 0) {
            throw new BusinessException("Fee must be less than amount", ErrorCode.INVALID_FEE);
        }
    }
    public void validateAccounts(Long user,Long merchant) {

        if (user == null || merchant == null) {
            throw new BusinessException("Account not found", ErrorCode.ACCOUNT_INVALID);
        }

        if (user.equals(merchant)) {
            throw new BusinessException("User and Merchant cannot be same", ErrorCode.ACCOUNT_INVALID);
        }
    }

//    private void validateAccountStatus(Account user, Account merchant) {
//
//        if (!user.isActive()) {
//            throw new BusinessException("User account is inactive", ErrorCode.ACCOUNT_BLOCKED);
//        }
//
//        if (!merchant.isActive()) {
//            throw new BusinessException("Merchant account is inactive", ErrorCode.ACCOUNT_BLOCKED);
//        }
//    }

}
