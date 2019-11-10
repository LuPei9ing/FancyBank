package com.company;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

public class ViewAccounts{

    Bank b;

    public ViewAccounts(Bank bb){
        b = bb;
    }
    public void place(BankCustomer bc){
        JFrame frame = new JFrame("FancyBank");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(false);

        frame.setSize(520, 400);
        JPanel panel = new JPanel();
        frame.getContentPane().removeAll();

        panel.setLayout(null);
        
        //get accounts
        SQLConnection sc = new SQLConnection();
		sc.TheSqlConnection();
        ArrayList<Account> allAccs = sc.getAccounts(bc.getUsername());

        String cols[] = {"Account Type", "Account Number", "Balance"};
        DefaultTableModel tabelModel = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        if(allAccs.size() != 0) {
        	for(int i = 0;i < allAccs.size();i++) {
        		Object[] obj = {allAccs.get(i).getAccountType(), allAccs.get(i).getIndex(), allAccs.get(i).getBalance()};
                tabelModel.addRow(obj);
        	}
        }

        JTable table = new JTable(tabelModel);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

//        DefaultListModel<String> l1 = new DefaultListModel<>();
//
//        for(Account a: allAccs){
//            l1.addElement(a.toString());
//        }
//
//        JList<String> list = new JList<>(l1);
//
//        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        list.setLayoutOrientation(JList.VERTICAL);

        JScrollPane listScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        listScroller.setPreferredSize(new Dimension(330, 300));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                listScroller,
                panel);

        JButton backButton = new JButton("Back");
        backButton.setBounds(10, 10, 140, 25);
        panel.add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                b.customerView(bc);
                frame.dispose();
            }
        });

        JButton addAccount = new JButton("Add Account");
        addAccount.setBounds(10, 40, 140, 25);
        panel.add(addAccount);

        addAccount.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                String[] options = {"Savings", "Checking", "Loan"};
                String str = (String)JOptionPane.showInputDialog(source, "What kind of account?", "Account Type", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                String amt = JOptionPane.showInputDialog("Enter initial amount: ");
                Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                try {
                    if (amt != null && !amt.isEmpty()) {
                        Double amt1 = Double.parseDouble(amt);
                        if (c != null) {
                        	//connect to db
                        	SQLConnection sc = new SQLConnection();
                    		sc.TheSqlConnection();
                            boolean check = checkEnough(amt1, b.getOpenAccountFee(), c);
                            if (str == "Savings") {
                                if (check) {
                                    Savings newAcc = new Savings(amt1, c, b.getOpenAccountFee(), b.getSavingsInterestRate(), b.getSavingsMinAmount());
                                    sc.addAccount("Savings", bc.getUsername(), Double.toString(newAcc.getRate()), Double.toString(newAcc.getBalance()));
                                    Account newAccount = sc.getAccounts(bc.getUsername()).get(sc.getAccounts(bc.getUsername()).size() - 1);
                                    int accountNum = newAccount.getIndex();
                                    String balance = Double.toString(newAccount.getBalance());
                                    sc.addTransaction(accountNum, "Savings", "Created Account", "0", balance, Double.toString(b.getOpenAccountFee()));
//                                    newAcc.addTransaction(new Transaction("Savings", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
//                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Savings", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
//                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
//                                    bc.addAccount(newAcc.getIndex(), newAcc);
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a savings account. The open fee is " + b.getOpenAccountFee());
                                }
                            } else if (str == "Checking") {
                                if (check) {
                                    Checking newAcc = new Checking(amt1, c, b.getOpenAccountFee(), 0);
                                    sc.addAccount("Checking", bc.getUsername(), Double.toString(newAcc.getRate()), Double.toString(newAcc.getBalance()));
                                    Account newAccount = sc.getAccounts(bc.getUsername()).get(sc.getAccounts(bc.getUsername()).size() - 1);
                                    int accountNum = newAccount.getIndex();
                                    String balance = Double.toString(newAccount.getBalance());
                                    sc.addTransaction(accountNum, "Checking", "Created Account", "0", balance, Double.toString(b.getOpenAccountFee()));
//                                    newAcc.addTransaction(new Transaction("Checking", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
//                                    bc.addAccount(newAcc.getIndex(), newAcc);
//                                    b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Checking", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
//                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a checking account. The open fee is " + b.getOpenAccountFee());
                                }
                            } else {
                                if (check) {
                                    double total = 0;
                                    ArrayList<Account> accs = sc.getAccounts(bc.getUsername());
//                                    Collection<Account> accs = bc.getAllAccounts();
                                    for(int i = 0;i < accs.size();i++) {
                                    	total += accs.get(i).getBalance();
                                    }
//                                    for (Account a: accs){
//                                        total += a.getBalance();
//                                    }
                                    if ((amt1 * c.getConversionToBaseRate()) <= (total * b.getMaxLoanPercent())) {
                                        Loan newAcc = new Loan(-amt1, c, b.getOpenAccountFee(), b.getLoanInterestRate());
                                        sc.addAccount("Loan", bc.getUsername(), Double.toString(newAcc.getRate()), Double.toString(newAcc.getBalance()));
                                        Account newAccount = sc.getAccounts(bc.getUsername()).get(sc.getAccounts(bc.getUsername()).size() - 1);
                                        int accountNum = newAccount.getIndex();
                                        String balance = Double.toString(newAccount.getBalance());
                                        sc.addTransaction(accountNum, "Loan", "Created Account", "0", balance, Double.toString(b.getOpenAccountFee()));
//                                        newAcc.addTransaction(new Transaction("Loan", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
//                                        bc.addAccount(newAcc.getIndex(), newAcc);
//                                        b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Loan", newAcc.getIndex(), "Created Account", 0, newAcc.getBalance(), b.getOpenAccountFee()));
//                                        b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        place(bc);
                                        frame.dispose();
                                    }
                                    else{
                                        JOptionPane.showMessageDialog(source, "Not enough capital for this loan. You must have at least " + amt1*c.getConversionToBaseRate() + " for a loan of size " + amt1*c.getConversionToBaseRate() + ".");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(source, "Not enough initial funds for a loan account. The open fee is " + b.getOpenAccountFee());
                                }
                            }
                            sc.close();
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Didn't select a currency");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(source, "Didn't enter a number for initial account value!");
                    }
                }
                catch (NumberFormatException n){
                    JOptionPane.showMessageDialog(source,"Did not enter a valid amount");
                }
            }
        });

        JButton deleteAccount = new JButton("Delete Account");
        deleteAccount.setBounds(10, 70, 140, 25);
        panel.add(deleteAccount);

        deleteAccount.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                	SQLConnection sc = new SQLConnection();
            		sc.TheSqlConnection();
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    Account account = sc.getAccount(account_index);
                    boolean checkBalance = checkEnough(account.getBalance(), b.getCloseAccountFee());
                    if (checkBalance) {
                    	//sc.addTransaction(account_index, account.getAccountType(), "Account Deletion", Double.toString(account.getBalance()), "0", Double.toString(b.getOpenAccountFee()));
                    	sc.deleteAccount(account_index);
                    	place(bc);
                    	frame.dispose();
//                        Transaction check = bc.removeAccount(account_index, b.getCloseAccountFee());
//                        if (check != null) {
//                            JOptionPane.showMessageDialog(source, "Account deleted");
//                            b.recentTransactions.put(b.getTransactionCounter(), check);
//                            b.setTransactionCounter(b.getTransactionCounter() + 1);
//                            place(bc);
//                            frame.dispose();
//                        } else {
//                            JOptionPane.showMessageDialog(source, "Account did not exist or did not belong to this customer");
//                        }
                    } else {
                        JOptionPane.showMessageDialog(source, "Insufficient funds in account to delete. The fee is: " + b.getCloseAccountFee());
                    }
                    sc.close();
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select a row.");
                }
        }
        });

        JButton addButton = new JButton("Deposit");
        addButton.setBounds(10, 100, 140, 25);
        panel.add(addButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    String amtS = JOptionPane.showInputDialog("Enter amount to deposit: ");
                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            if (c != null) {
                            	SQLConnection sc = new SQLConnection();
                        		sc.TheSqlConnection();
                                double amt = Double.parseDouble(amtS);
                                if (amt > 0) {
                                	Account account = sc.getAccount(account_index);
                                    if (account.getAccountType().equals("Checking")) {
                                        if (checkEnough(amt, b.getCheckingAccountTransactionFee(), c)) {
                                        	double initB = account.getBalance();
                                        	double finalAmt = (amt * c.getConversionToBaseRate()) - b.getCheckingAccountTransactionFee();
                                        	double finalB = initB + finalAmt;
                                        	sc.updateAccount(account_index, finalB);
                                            sc.addTransaction(account_index, "Checking", "Deposit", Double.toString(initB), Double.toString(finalB), Double.toString(b.getCheckingAccountTransactionFee()));
//                                            double initB = bc.getAccount(account_index).getBalance();
//                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()) - b.getCheckingAccountTransactionFee());
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
//                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
//                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Deposit amount is less than the checking account transaction fee.");
                                        }
                                    } else {
                                    	double initB = account.getBalance();
                                    	double finalAmt = amt * c.getConversionToBaseRate();
                                    	double finalB = initB + finalAmt;
                                        if (account.getAccountType().equals("Savings")) {
                                        	sc.updateAccount(account_index, finalB);
                                        	sc.updateAccount(account_index, finalB);
                                            sc.addTransaction(account_index, "Savings", "Deposit", Double.toString(initB), Double.toString(finalB), "0");
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        }
                                        else{
                                        	sc.updateAccount(account_index, finalB);
                                            sc.addTransaction(account_index, "Loan", "Deposit", Double.toString(initB), Double.toString(finalB), "0");
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Loan", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Loan", bc.getAccount(account_index).getIndex(), "Deposit", initB, bc.getAccount(account_index).getBalance(), 0));
//                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        }
                                    }
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Deposit amount is less than or equal to 0.");
                                }
                                sc.close();
                            }
                            else{
                                JOptionPane.showMessageDialog(source, "Did not enter a currency.");
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Did not enter an amount to deposit.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid amount to deposit.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.setBounds(10, 130, 140, 25);
        panel.add(withdrawButton);

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    String amtS = JOptionPane.showInputDialog("Enter amount to withdraw: ");
                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                        	SQLConnection sc = new SQLConnection();
                    		sc.TheSqlConnection();
                            double amt = Double.parseDouble(amtS);
                            if (c != null) {
                                if (amt > 0) {
                                	Account account = sc.getAccount(account_index);
                                    if (account.getBalance() >= (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee()) {
                                        if (account.getAccountType().equals("Checking")) {
                                            if (account.getBalance() >= (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee() + b.getCheckingAccountTransactionFee()) {
                                            	double initB = account.getBalance();
                                            	double finalAmt = amt * c.getConversionToBaseRate() + b.getWithdrawalFee() + b.getCheckingAccountTransactionFee();
                                            	double finalB = initB - finalAmt;
                                            	sc.updateAccount(account_index, finalB);
                                            	sc.addTransaction(account_index, "Checking", "Withdraw", Double.toString(initB), Double.toString(finalB), Double.toString(b.getWithdrawalFee() + b.getCheckingAccountTransactionFee()));
//                                                double initB = bc.getAccount(account_index).getBalance();
//                                                bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + b.getWithdrawalFee() + b.getCheckingAccountTransactionFee());
//                                                bc.getAccount(account_index).addTransaction(new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()+b.getCheckingAccountTransactionFee()));
//                                                b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()+b.getCheckingAccountTransactionFee()));
//                                                b.setTransactionCounter(b.getTransactionCounter() + 1);
                                            } else {
                                                JOptionPane.showMessageDialog(source, "Insufficient funds. The withdrawal fee is " + b.getWithdrawalFee() + ", and the checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                            }
                                        } else if (account.getAccountType().equals("Savings")) {
                                        	double initB = account.getBalance();
                                        	double finalAmt = (amt * c.getConversionToBaseRate()) + b.getWithdrawalFee();
                                        	double finalB = initB - finalAmt;
                                        	sc.updateAccount(account_index, finalB);
                                        	sc.addTransaction(account_index, "Savings", "Withdraw", Double.toString(initB), Double.toString(finalB), Double.toString(b.getWithdrawalFee()));
//                                            double initB = bc.getAccount(account_index).getBalance();
//                                            bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + b.getWithdrawalFee());
//                                            bc.getAccount(account_index).addTransaction(new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()));
//                                            b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Withdraw", initB, bc.getAccount(account_index).getBalance(), b.getWithdrawalFee()));
//                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Cannot withdraw from a Loan");
                                        }
                                        place(bc);
                                        frame.dispose();
                                    } else {
                                        JOptionPane.showMessageDialog(source, "Insufficient funds. The withdrawal fee is " + b.getWithdrawalFee() + ".");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(source, "Withdrawal amount is less than or equal to 0.");
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(source, "Did not enter a currency.");
                            }
                            sc.close();
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Did not enter a number to withdraw.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid amount to withdraw.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton transferFromButton = new JButton("Transfer from");
        transferFromButton.setBounds(10, 160, 140, 25);
        panel.add(transferFromButton);

        //subtract from current selection
        transferFromButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    String amtS = JOptionPane.showInputDialog("Enter amount to transfer: ");
                    Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                    try {
                        if (amtS != null && !amtS.isEmpty()) {
                            double amt = Double.parseDouble(amtS);
                            if (c != null) {
                                if (amt > 0) {
                                    if (bc.getAccount(account_index) instanceof Checking) {
                                        if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee()) {
                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                                            Integer alt_index = Integer.valueOf(altAccS);
                                            if (alt_index != account_index) {
                                                Account a = bc.getAccount(alt_index);
                                                if (a != null) {
                                                    double initBa = a.getBalance();
                                                    double initB = bc.getAccount(account_index).getBalance();
                                                    bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee());
                                                    bc.getAccount(alt_index).addBalance((amt * c.getConversionToBaseRate()));
                                                  //  bc.getAccount(account_index).addTransaction(new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
                                                 //   b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Checking", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), b.getCheckingAccountTransactionFee()));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                  //  a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                 //   b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                } else {
                                                    JOptionPane.showMessageDialog(source, "Other account does not exist or does not belong to the user.");
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(source, "Entered the same account number");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds. The checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                        }
                                    } else if (bc.getAccount(account_index) instanceof Savings) {
                                        if (bc.getAccount(account_index).getBalance() >= (amt * c.getConversionToBaseRate())) {
                                            String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                                            Integer alt_index = Integer.valueOf(altAccS);
                                            Account a = bc.getAccount(alt_index);
                                            if (alt_index != account_index) {
                                                if (a != null) {
                                                    double initBa = a.getBalance();
                                                    double initB = bc.getAccount(account_index).getBalance();
                                                    bc.getAccount(account_index).removeBalance((amt * c.getConversionToBaseRate()));
                                                    bc.getAccount(alt_index).addBalance((amt * c.getConversionToBaseRate()));
                                                   // bc.getAccount(account_index).addTransaction(new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), 0));
                                                  //  b.recentTransactions.put(b.getTransactionCounter(), new Transaction("Savings", bc.getAccount(account_index).getIndex(), "Transfer from", initB, bc.getAccount(account_index).getBalance(), 0));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                  //  a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                  //  b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer to", initBa, a.getBalance(), 0));
                                                    b.setTransactionCounter(b.getTransactionCounter() + 1);
                                                } else {
                                                    JOptionPane.showMessageDialog(source, "Other account does not exist or does not belong to the user.");
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(source, "Entered the same account number");
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds.");
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(source, "Cannot use this function with a loan account.");
                                    }
                                    place(bc);
                                    frame.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(source, "Transfer amount is less than or equal to 0.");
                                }
                            }
                            else {
                                JOptionPane.showMessageDialog(source, "Didn't pick a currency.");
                            }
                        }
                        else{
                            JOptionPane.showMessageDialog(source, "Did not enter a valid number to transfer.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid number.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton transferTo = new JButton("Transfer to");
        transferTo.setBounds(10, 190, 140, 25);
        panel.add(transferTo);

        //add to current selection
        transferTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                    String altAccS = JOptionPane.showInputDialog("Enter account number of other account: ");
                    try {
                        Integer alt_index = Integer.valueOf(altAccS);
                        Account a = bc.getAccount(alt_index);
                        Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                        if (a != null) {
                            if (a instanceof Loan) {
                                JOptionPane.showMessageDialog(source, "Account given is a loan.");
                            } else {
                                String amtS = JOptionPane.showInputDialog("Enter amount to transfer: ");
                                Currency c = (Currency)JOptionPane.showInputDialog(source, "Which currency", "Currency Selection", JOptionPane.QUESTION_MESSAGE, null, b.getAllCurrencies(), b.getAllCurrencies()[0]);
                                double amt = Double.parseDouble(amtS);
                                if (c != null) {
                                    if (a instanceof Checking) {
                                        if (a.getBalance() >= (amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee()) {
                                            double initBa = a.getBalance();
                                            double initB = bc.getAccount(account_index).getBalance();
                                            a.removeBalance((amt * c.getConversionToBaseRate()) + b.getCheckingAccountTransactionFee());
                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
                                            //bc.getAccount(account_index).addTransaction(new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                            //b.recentTransactions.put(b.getTransactionCounter(), new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                            //a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), b.getCheckingAccountTransactionFee()));
                                           // b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), b.getCheckingAccountTransactionFee()));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds. The checking account transaction fee is " + b.getCheckingAccountTransactionFee() + ".");
                                        }
                                    } else {
                                        if (a.getBalance() >= (amt * c.getConversionToBaseRate())) {
                                            double initBa = a.getBalance();
                                            double initB = bc.getAccount(account_index).getBalance();
                                            a.removeBalance((amt * c.getConversionToBaseRate()));
                                            bc.getAccount(account_index).addBalance((amt * c.getConversionToBaseRate()));
                                           // bc.getAccount(account_index).addTransaction(new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                           // b.recentTransactions.put(b.getTransactionCounter(), new Transaction(bc.getAccount(account_index).getAccountType(), bc.getAccount(account_index).getIndex(), "Transfer to", initB, bc.getAccount(account_index).getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                           // a.addTransaction(new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), 0));
                                           // b.recentTransactions.put(b.getTransactionCounter(), new Transaction(a.getAccountType(), a.getIndex(), "Transfer from", initBa, a.getBalance(), 0));
                                            b.setTransactionCounter(b.getTransactionCounter() + 1);
                                        } else {
                                            JOptionPane.showMessageDialog(source, "Insufficient funds.");
                                        }
                                    }
                                    place(bc);
                                    frame.dispose();
                                }
                                else{
                                    JOptionPane.showMessageDialog(source, "Did not enter a currency.");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(source, "Invalid account number.");
                        }
                    }
                    catch(NumberFormatException e1){
                        JOptionPane.showMessageDialog(source, "Enter a valid number.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton viewTs = new JButton("View Transactions");
        viewTs.setBounds(10, 220, 140, 25);
        panel.add(viewTs);


        viewTs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton source = (JButton) e.getSource();
                int selected = table.getSelectedRow();
                if (selected != -1) {
                	Integer account_index = (Integer) tabelModel.getValueAt(selected, 1);
                    ViewTransactions vt = new ViewTransactions(b);
                    vt.place(bc, account_index);
                    frame.dispose();
                    //viewTransactions1Acc(frame, bc, bc.getAccount(account_index).getAllTransactions());
                }
                else{
                    JOptionPane.showMessageDialog(source, "Please select an account from the list.");
                }
            }
        });

        JButton resolveLoans = new JButton("Resolve Loans");
        resolveLoans.setBounds(10, 250, 140, 25);
        panel.add(resolveLoans);


        resolveLoans.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                JButton source = (JButton) e.getSource();
//                Collection<Account> accs = bc.getAllAccounts();
//                ArrayList<Integer> indexes = new ArrayList<>();
//                for (Account a:accs){
//                    if (a instanceof Loan){
//                        if (a.getBalance() >= b.getCloseAccountFee()){
//                            indexes.add(a.getIndex());
//                        }
//                    }
//                }
//                for (int i=0; i<indexes.size(); i++){
//                    Transaction check = bc.removeAccount(indexes.get(i), b.getCloseAccountFee());
//                    if (check != null) {
//                        b.recentTransactions.put(b.getTransactionCounter(), check);
//                        b.setTransactionCounter(b.getTransactionCounter() + 1);
//                    }
//                }
//                JOptionPane.showMessageDialog(source, "All loans with balances greater than the close account fee: " + b.getCloseAccountFee() + " were closed.");
//                place(bc);
//                frame.dispose();
            }
        });




        frame.getContentPane().add(splitPane);
        frame.setVisible(true);
    }
    

        public boolean checkEnough(double amt, double comparison, Currency c){
        if ((amt * c.getConversionToBaseRate()) > comparison){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkEnough(double amt, double comparison){
        if ((amt) > comparison){
            return true;
        }
        else {
            return false;
        }
    }
}
