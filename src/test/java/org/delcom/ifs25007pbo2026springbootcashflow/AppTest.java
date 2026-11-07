package org.delcom.ifs25007pbo2026springbootcashflow;

import org.delcom.ifs25007pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25007pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25007pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AppTest {
    @Autowired
    private ITransactionRepository repository;

    @Autowired
    private FinanceUseCase useCase;

    @Autowired
    private FinancePresenter presenter;

    @Test
    void beansTerpasang() {
        assertNotNull(repository);
        assertNotNull(useCase);
        assertNotNull(presenter);
    }
}
