import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main
 */
public class Main {
    /**
     * MonthlyReport
     */
    public static Integer year = 2024;
    public static String path = "reports/";

    public static void PrintMenu() {
        System.out.println("MENU");
        System.out.println("1. Process all monthly reports."); // считать все месячные отчеты
        System.out.println("2. Process the annual report."); // считать годовой отчет
        System.out.println("3. Verify the reports."); // Сверить отчеты
        System.out.println("4. Extract information from all monthly reports."); // Вывести информацию о всех месячных
                                                                                // отчетах
        System.out.println("5. Extract information from the annual report."); // Вывести информацию о годовом отчете
        System.out.println("6. EXIT");
    }

    public static ArrayList<MonthlyReport> readAllMonthReports(Main mainInstance) {
        ArrayList<MonthlyReport> mas = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            String num;
            if (i < 10) {
                num = "0" + i;
            } else {
                num = "" + i;
            }
            String pathFile = path + "m." + year + num + ".csv";
            File file = new File(pathFile); // Создаем объект файла
            if (file.exists()) {
                String fileContents = readFileContentsOrNull(pathFile);
                if (fileContents != null) {
                    String fileName = pathFile.substring(8, pathFile.length() - 4);

                    String lines[] = fileContents.split("\\n");

                    MonthlyReport monthlyReport = mainInstance.new MonthlyReport(lines);
                    monthlyReport.setMonth(i);
                    mas.add(monthlyReport);

                } else {
                    // нет такого файла
                }
            }
        }
        return mas;
    }

    public static YearlyReport readYearReport(Main mainInstance) {
        String pathFile = path + "y." + year + ".csv";
        File file = new File(pathFile); // Создаем объект файла
        if (file.exists()) {
            String fileContents = readFileContentsOrNull(pathFile);
            if (fileContents != null) {
                String fileName = pathFile.substring(8, pathFile.length() - 4);

                String lines[] = fileContents.split("\\n");

                YearlyReport yearlyReport = mainInstance.new YearlyReport(lines);

                return yearlyReport;

            } else {
                // нет такого файла
            }
        }
        return null;
    }

    public static void sverka(ArrayList<MonthlyReport> mnths, YearlyReport yearlyReport) {
        // вытащим все месяца из годового отчета
        HashMap<Integer, YearlyReport.MonthFromYearReport> mas = yearlyReport.mas;
        for (Integer mes : mas.keySet()) {
            YearlyReport.MonthFromYearReport data = mas.get(mes);
            if (data != null) {
                Integer trataYear = data.getTrata();
                Integer pribilYear = data.getPribil();
                for (MonthlyReport monthlyReport : mnths) {
                    Integer month = monthlyReport.getMonth();
                    if (month != null && month == mes) {
                        int trataM = monthlyReport.getTrata();
                        int pribilM = monthlyReport.getPribil();
                        if (trataM != trataYear) {
                            System.out.println("Month: " + mes
                                    + " The expenditure does not match in the monthly report and the yearly report. "
                                    + trataM
                                    + "  " + trataYear);
                        } else if (pribilM != pribilYear) {
                            System.out.println("Month: " + mes
                                    + " The profit does not match in the monthly report and the yearly report "
                                    + pribilM
                                    + "  " + pribilYear);
                        } else {
                            System.out.println("All ok");
                        }
                    }
                }
            }
        }
    }

    public static void infoAllReportsForMonths(ArrayList<MonthlyReport> mnths) {
        // название месяца
        // самый прибыльный товар
        // самая большая трата

        for (MonthlyReport monthlyReport : mnths) {
            String mesName;
            Integer month = monthlyReport.getMonth();

            switch (month) {
                case 1:
                    mesName = "January";
                    break;
                case 2:
                    mesName = "February";
                    break;
                case 3:
                    mesName = "March";
                    break;
                case 4:
                    mesName = "April";
                    break;
                case 5:
                    mesName = "May";
                    break;
                case 6:
                    mesName = "June";
                    break;
                case 7:
                    mesName = "July";
                    break;
                case 8:
                    mesName = "August";
                    break;
                case 9:
                    mesName = "September";
                    break;
                case 10:
                    mesName = "October";
                    break;
                case 11:
                    mesName = "November";
                    break;
                case 12:
                    mesName = "December";
                    break;
                default:
                    mesName = "Invalid month";
                    break;
            }
            ArrayList<MonthlyReport.MonthlyReportData> mas = monthlyReport.mas;
            MonthlyReport.MonthlyReportData OverTrata = null;
            MonthlyReport.MonthlyReportData OverPribil = null;
            Integer trataAll = 0;
            Integer pribilAll = 0;

            for (MonthlyReport.MonthlyReportData data : mas) {
                Boolean is_expense = data.getIs_expense();
                Integer sum = data.getSum_of_one();
                Integer count = data.getQuantity();
                Integer summaAll = sum * count;
                if (is_expense == true) {
                    trataAll = summaAll;
                    if (OverTrata != null) {
                        Integer sumLocal = OverTrata.getSum_of_one();
                        Integer quantLocal = OverTrata.getQuantity();
                        Integer trataLocal = sumLocal * quantLocal;
                        if (trataLocal > trataAll) {
                            OverTrata = data;
                        }
                    } else {
                        OverTrata = data;
                    }

                } else {
                    pribilAll = summaAll;
                    if (OverPribil != null) {
                        Integer sumLocal = OverPribil.getSum_of_one();
                        Integer quantLocal = OverPribil.getQuantity();
                        Integer pribilLocal = sumLocal * quantLocal;
                        if (pribilLocal > pribilAll) {
                            OverPribil = data;
                        }
                    } else {
                        OverPribil = data;
                    }
                }
            }
            String trataText = "";
            String pribilText = "";
            if (OverTrata != null) {
                trataText = OverTrata.item_name + " " + OverTrata.sum_of_one + " " + OverTrata.quantity;
            }

            if (OverPribil != null) {
                pribilText = OverPribil.item_name + " " + OverPribil.sum_of_one + " " + OverPribil.quantity;
            }
            System.out.println(
                    mesName + " The largest expense: " + trataText + " The largest profit: " + pribilText);
        }
    }

    public static void infoYearRepost(YearlyReport report) {
        // год
        // прибыль по кжадому месяцу
        // средний расход по каждому месяцу
        // средний доход по кжадому месяцу
        System.out.println("YEAR: " + year);
        HashMap<Integer, YearlyReport.MonthFromYearReport> mas = report.mas;
        for (Integer mes : mas.keySet()) {
            YearlyReport.MonthFromYearReport data = mas.get(mes);
            if (data != null) {
                Integer trata = data.getTrata();
                Integer pribil = data.getPribil();
                Integer benefit = pribil - trata;
                String text = data.getInfo();
                text = text + " Income: " + benefit;
                System.out.println(text);
            }

        }
    }

    public class MonthlyReport {
        int month;
        ArrayList<MonthlyReportData> mas;
        int trata = 0;
        int pribil = 0;

        MonthlyReport(String lines[]) {
            mas = new ArrayList<>();
            int n = 0;
            int genTrata = 0;
            int genPribil = 0;
            for (String line : lines) {
                if (n == 0) {
                    n++;
                } else {
                    String item;
                    Boolean expense;
                    Integer quant;
                    Integer sum;
                    String lineContents[] = line.split(",");
                    item = lineContents[0];
                    expense = Boolean.parseBoolean(lineContents[1]);
                    quant = Integer.parseInt(lineContents[2]);
                    sum = Integer.parseInt(lineContents[3]);
                    if (expense == true) {
                        genTrata = genTrata + (sum * quant);

                    } else {
                        genPribil = genPribil + (sum + quant);
                    }
                    MonthlyReportData mnthData = new MonthlyReportData(item, expense, quant, sum);
                    this.mas.add(mnthData);
                    n++;
                }

            }
            this.setPribil(genPribil);
            this.setTrata(genTrata);
        }

        public int getPribil() {
            return pribil;
        }

        public void setPribil(int pribil) {
            this.pribil = pribil;
        }

        public int getTrata() {
            return trata;
        }

        public void setTrata(int trata) {
            this.trata = trata;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public void printMonthReport() {
            ArrayList<MonthlyReportData> mass = this.mas;

            for (MonthlyReportData monthlyReportData : mass) {
                String textAbout = "Item: " + monthlyReportData.item_name + ", is_expense: "
                        + monthlyReportData.is_expense + ", quantity: " + monthlyReportData.is_expense
                        + ", sum_of_one: " + monthlyReportData.sum_of_one;
                System.out.println(textAbout);
            }
        }

        /**
         * MonthlyReportData
         */
        public class MonthlyReportData {
            String item_name;
            Boolean is_expense;
            Integer quantity;
            Integer sum_of_one;

            MonthlyReportData(String item, Boolean expense, Integer quan, Integer sum) {
                this.item_name = item;
                this.is_expense = expense;
                this.quantity = quan;
                this.sum_of_one = sum;
            }

            public Boolean getIs_expense() {
                return is_expense;
            }

            public String getItem_name() {
                return item_name;
            }

            public Integer getQuantity() {
                return quantity;
            }

            public Integer getSum_of_one() {
                return sum_of_one;
            }
        }

    }

    /**
     * YearlyReport
     */
    public class YearlyReport {
        HashMap<Integer, MonthFromYearReport> mas;

        YearlyReport(String lines[]) {
            mas = new HashMap<>();
            int n = 0;
            for (String line : lines) {
                if (n == 0) {
                    n++;
                } else {
                    Integer month;
                    Integer sum;
                    Boolean is_expense;
                    String lineContents[] = line.split(",");
                    month = Integer.parseInt(lineContents[0]);
                    sum = Integer.parseInt(lineContents[1]);
                    is_expense = Boolean.parseBoolean(lineContents[2]);
                    this.setData(month, sum, is_expense);

                    n++;
                }

            }
        }

        public void setData(Integer month, Integer sum, Boolean is_expense) {
            HashMap<Integer, MonthFromYearReport> massiv = this.mas;
            MonthFromYearReport mData = massiv.get(month);
            if (mData != null) {
                if (is_expense == true) {
                    mData.setTrata(sum);
                } else {
                    mData.setPribil(sum);
                    ;
                }
            } else {
                Integer trata = 0;
                Integer pribil = 0;
                if (is_expense == true) {
                    trata = sum;
                } else {
                    pribil = sum;
                }
                MonthFromYearReport mDataNew = new MonthFromYearReport(month, trata, pribil);
                massiv.put(month, mDataNew);
            }
        }

        /**
         * MonthFromYearReport
         */
        public class MonthFromYearReport {
            Integer month;
            Integer trata;
            Integer pribil;

            MonthFromYearReport(Integer mnth, Integer trat, Integer prib) {
                this.month = mnth;
                this.pribil = prib;
                this.trata = trat;
            }

            public Integer getMonth() {
                return month;
            }

            public void setMonth(Integer month) {
                this.month = month;
            }

            public Integer getPribil() {
                return pribil;
            }

            public void setPribil(Integer pribil) {
                this.pribil = pribil;
            }

            public Integer getTrata() {
                return trata;
            }

            public void setTrata(Integer trata) {
                this.trata = trata;
            }

            public String getInfo() {
                Integer month = this.getMonth();
                Integer pribil = this.getPribil();
                Integer trata = this.getTrata();
                String text = "Month: " + month + ", Expense: " + trata + ", Profit: " + pribil;
                return text;
            }
        }

    }

    private static String readFileContentsOrNull(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path)); // Read all lines from the file
            StringBuilder contentBuilder = new StringBuilder();
            for (String line : lines) {
                contentBuilder.append(line).append("\n"); // Append each line to StringBuilder
            }
            return contentBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null in case of exception
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        ArrayList<MonthlyReport> mnthsReports = null;
        YearlyReport yearlyReport = null;
        Scanner scanner = new Scanner(System.in);
        int userInput = 0;
        Main.PrintMenu();
        userInput = scanner.nextInt();
        while (userInput != 0) {

            if (userInput == 6) {
                break;
            } else if (userInput == 1) {
                mnthsReports = readAllMonthReports(main);
            } else if (userInput == 2) {
                yearlyReport = readYearReport(main);
            } else if (userInput == 3) {
                if (mnthsReports != null && yearlyReport != null) {
                    Main.sverka(mnthsReports, yearlyReport);
                } else {
                    if (mnthsReports == null) {
                        System.err.println("We need to input monthly reports.");
                    }
                    if (yearlyReport == null) {
                        System.err.println("We need to input yearly reports.");
                    }
                }
                
            } else if (userInput == 4) {
                if (mnthsReports != null) {
                    Main.infoAllReportsForMonths(mnthsReports);
                } else {
                    System.err.println("We need to input monthly reports.");
                }
                
            } else if (userInput == 5) {
                if (yearlyReport != null) {
                    Main.infoYearRepost(yearlyReport);
                } else {
                    System.err.println("We need to input yearly reports.");
                }
                
            }

            Main.PrintMenu();
            userInput = scanner.nextInt();
        }

    }

}