package ru.make.account.load.util;

import ru.make.account.load.model.OperationDto;
import ru.make.account.load.model.TicketDirectionEnum;
import ru.make.account.load.model.TicketDto;
import ru.make.account.load.model.stuff.LoadData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoadUtil {
    private final String fileName;
    private final AccessUtil accessUtil;
    private final LoadData loadData;
    private final AccountUtil accountUtil;
    private final CategoryUtil categoryUtil;
    private final TicketUtil ticketUtil;

    public LoadUtil(String fileName,
                    AccessUtil accessUtil,
                    LoadData loadData,
                    AccountUtil accountUtil) {
        this.fileName = fileName;
        this.accessUtil = accessUtil;
        this.loadData = loadData;
        this.accountUtil = accountUtil;
        this.categoryUtil = new CategoryUtil(accessUtil, loadData);
        this.ticketUtil = new TicketUtil(accessUtil, loadData);
    }

    public void load() {
        File file = new File(fileName);

        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            accountUtil.initData();
            categoryUtil.initData(accountUtil.getAccount(loadData.getAccountName()).getId());

            TicketDto ticket = null;
            String line = null;
            List<OperationDto> minus = new ArrayList<>();
            List<OperationDto> plus = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                var array = line.split("[|]");

                var date = array[0];
                var sumMinus = array[1];
                var sumPlus = array[2];
                var operationName = array[3];
                var categoryName = array[4];

                if (Utils.nonNull(date)) {
                    if (ticket == null) {
                        var account = accountUtil.getAccount(loadData.getAccountName());
                        ticket = new TicketDto();
                        ticket.setAccountId(account.getId());
                        ticket.setDate(Utils.convert(date));
                        ticket.setUser(UUID.fromString(accessUtil.getUserId()));
                    } else {
                        if (!minus.isEmpty()) {
                            ticket.setTicketDirection(TicketDirectionEnum.EXPENDITURE);
                            ticket.setOperations(minus);
                            ticketUtil.saveTicket(ticket);
                            minus = new ArrayList<>();
                        }

                        if (!plus.isEmpty()) {
                            ticket.setTicketDirection(TicketDirectionEnum.INCOME);
                            ticket.setOperations(plus);
                            ticketUtil.saveTicket(ticket);
                            plus = new ArrayList<>();
                        }

                        var account = accountUtil.getAccount(loadData.getAccountName());
                        ticket = new TicketDto();
                        ticket.setAccountId(account.getId());
                        ticket.setDate(Utils.convert(date));
                        ticket.setUser(UUID.fromString(accessUtil.getUserId()));
                    }
                }

                OperationDto operation = new OperationDto();
                operation.setActive(Boolean.TRUE);
                operation.setCategory(categoryUtil.getCategory(categoryName));
                operation.setName(operationName);
                if (Utils.nonNull(sumMinus)) {
                    operation.setSum(Utils.convertNumber(sumMinus));
                    minus.add(operation);
                }
                if (Utils.nonNull(sumPlus)) {
                    operation.setSum(Utils.convertNumber(sumPlus));
                    plus.add(operation);
                }
            }

            if (!minus.isEmpty()) {
                ticket.setTicketDirection(TicketDirectionEnum.EXPENDITURE);
                ticket.setOperations(minus);
                ticketUtil.saveTicket(ticket);
            }

            if (!plus.isEmpty()) {
                ticket.setTicketDirection(TicketDirectionEnum.INCOME);
                ticket.setOperations(plus);
                ticketUtil.saveTicket(ticket);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
