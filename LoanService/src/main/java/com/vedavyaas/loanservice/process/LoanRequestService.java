package com.vedavyaas.loanservice.process;

import com.vedavyaas.loanservice.credit.LoanState;
import org.springframework.stereotype.Service;

@Service
public class LoanRequestService {
    private final LoanRequestRepository loanRequestRepository;

    public LoanRequestService(LoanRequestRepository loanRequestRepository) {
        this.loanRequestRepository = loanRequestRepository;
    }


    public LoanRequestEntity getAllLoans(String username) {
        return loanRequestRepository.findByUsername(username);
    }

    public String applyLoan(String username, Double loanAmount) {
        if (loanRequestRepository.existsByUsername(username)) {
            throw new DuplicateApplicationException("Loan already exists. You can have only one loan per account.");
        }

        LoanRequestEntity loanRequestEntity = new LoanRequestEntity(username, loanAmount);
        loanRequestRepository.save(loanRequestEntity);

        return "Your loan will be processed and verified if applicable.";
    }

    public String loanCancellationRequest(String username) {
        if (!loanRequestRepository.existsByUsername(username)) {
            throw new DuplicateApplicationException("Loan is not applied yet.");
        }

        LoanRequestEntity loanRequestEntity = loanRequestRepository.findByUsername(username);

        if (loanRequestEntity.getLoanProcessed().equals(LoanState.WAITING)) {
            loanRequestRepository.delete(loanRequestEntity);
            return "Loan has been cancelled.";
        }

        if (loanRequestEntity.getLoanProcessed().equals(LoanState.UPDATED)) {
            loanRequestRepository.delete(loanRequestEntity);
            return "Loan has been cancelled.";
        }

        if(loanRequestEntity.getLoanProcessed().equals(LoanState.FAILED)) {
            loanRequestRepository.delete(loanRequestEntity);
            return "Loan has been cancelled.";
        }

        return "Loan has been processed cannot be cancelled.";
    }
}
