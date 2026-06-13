package org.example.final_project.extension;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

public class SummaryController {

    @FXML
    private BarChart<String, Number> daysChart;

    @FXML
    private BarChart<String, Number> monthsChart;

    @FXML
    public void initialize() {
        setupDaysChart();
        setupMonthsChart();
    }

    private void setupDaysChart() {
        // Chuỗi dữ liệu Thu nhập theo ngày
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>("Mon", 1200));
        incomeSeries.getData().add(new XYChart.Data<>("Tue", 1500));
        incomeSeries.getData().add(new XYChart.Data<>("Wed", 800));
        incomeSeries.getData().add(new XYChart.Data<>("Thu", 2100));
        incomeSeries.getData().add(new XYChart.Data<>("Fri", 1700));
        incomeSeries.getData().add(new XYChart.Data<>("Sat", 900));
        incomeSeries.getData().add(new XYChart.Data<>("Sun", 1100));

        // Chuỗi dữ liệu Chi tiêu theo ngày
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");
        expenseSeries.getData().add(new XYChart.Data<>("Mon", 800));
        expenseSeries.getData().add(new XYChart.Data<>("Tue", 1100));
        expenseSeries.getData().add(new XYChart.Data<>("Wed", 950));
        expenseSeries.getData().add(new XYChart.Data<>("Thu", 1300));
        expenseSeries.getData().add(new XYChart.Data<>("Fri", 1400));
        expenseSeries.getData().add(new XYChart.Data<>("Sat", 1800));
        expenseSeries.getData().add(new XYChart.Data<>("Sun", 500));

        // Đổ dữ liệu vào biểu đồ Ngày
        daysChart.getData().addAll(incomeSeries, expenseSeries);
    }

    private void setupMonthsChart() {
        // Chuỗi dữ liệu Thu nhập theo tháng
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        incomeSeries.getData().add(new XYChart.Data<>("Jan", 32000));
        incomeSeries.getData().add(new XYChart.Data<>("Feb", 28000));
        incomeSeries.getData().add(new XYChart.Data<>("Mar", 35000));
        incomeSeries.getData().add(new XYChart.Data<>("Apr", 41000));
        incomeSeries.getData().add(new XYChart.Data<>("May", 39000));
        incomeSeries.getData().add(new XYChart.Data<>("Jun", 45000));
        incomeSeries.getData().add(new XYChart.Data<>("Jul", 48000));

        // Chuỗi dữ liệu Chi tiêu theo tháng
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");
        expenseSeries.getData().add(new XYChart.Data<>("Jan", 25000));
        expenseSeries.getData().add(new XYChart.Data<>("Feb", 22000));
        expenseSeries.getData().add(new XYChart.Data<>("Mar", 27000));
        expenseSeries.getData().add(new XYChart.Data<>("Apr", 30000));
        expenseSeries.getData().add(new XYChart.Data<>("May", 31000));
        expenseSeries.getData().add(new XYChart.Data<>("Jun", 29000));
        expenseSeries.getData().add(new XYChart.Data<>("Jul", 34000));

        // Đổ dữ liệu vào biểu đồ Tháng
        monthsChart.getData().addAll(incomeSeries, expenseSeries);
    }
}