package com.banking.account.cmd.api.command;

import com.banking.account.cmd.domain.AccountAggregate;
import com.banking.cqrs.core.handlers.EventSourcingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCommandHandler implements CommandHandler{
    @Autowired
    private EventSourcingHandler<AccountAggregate> eventSourcingHandler;
    @Override
    public void handle(OpenAccountCommand command) {
        var aggregate = new AccountAggregate(command);//Estoy instanciando
        eventSourcingHandler.save(aggregate);
    }

    @Override
    public void handle(DepositFoundsCommand command) {
        var aggregate = eventSourcingHandler.getById(command.getId());//Representa la cuenta de banco
        aggregate.depositFunds(command.getAmount());
        eventSourcingHandler.save((aggregate));//Cada vez que cambie esta teniendo un nuevo estado
    }

    @Override
    public void handle(WithdrawFoundsCommand command) {
        var aggregate = eventSourcingHandler.getById(command.getId());
        if (command.getAmount() > aggregate.getBalance()){//Intento retirar un dinero que no existe
            throw new IllegalStateException("Insuficientes fondos, no se puede retirar dinero");
        }//Si el monto es mayor que la cuenta de banco

        aggregate.withdrawFunds(command.getAmount());
        eventSourcingHandler.save(aggregate);
    }

    @Override
    public void handle(CloseAccountCommand command) {
        var aggregate = eventSourcingHandler.getById(command.getId());
        aggregate.closeAccount();
        eventSourcingHandler.save(aggregate);
    }
}
