package com.dcy.mockiothing.platform.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("deviceMockDao")
public class DeviceMockDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createDeviceTable(String deviceModelName, Map<String, String> deviceDataPoints) {
        if (validateTableExist(deviceModelName)) {
            deleteDeviceTable(deviceModelName, null);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName(deviceModelName)).append(" (");
        for (Map.Entry<String, String> entry : deviceDataPoints.entrySet()) {
            if (entry.getKey().equals("uuid")) {
                sb.append(" `uuid` VARCHAR(100) NOT NULL,");
            } else {
                sb.append(" `").append(entry.getKey()).append("` VARCHAR(100) DEFAULT '',");
            }
        }
        sb.append(" `update_time` TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP DEFAULT CURRENT_TIMESTAMP,");
        sb.append(" PRIMARY KEY(`uuid`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        try {
            jdbcTemplate.update(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertDeviceTable(String deviceModelName, Map<String, String> deviceDataPoints) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName(deviceModelName)).append(" (");
        for (Map.Entry<String, String> entry : deviceDataPoints.entrySet()) {
            sb.append(entry.getKey()).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(") VALUES (");
        for (Map.Entry<String, String> entry : deviceDataPoints.entrySet()) {
            sb.append(entry.getValue()).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(");");
        try {
            jdbcTemplate.update(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDeviceTable(String deviceModelName, Map<String, String> deviceDataPoints, String id) {
        if (deviceDataPoints == null || deviceDataPoints.isEmpty()) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(tableName(deviceModelName)).append(" SET ");
        for (Map.Entry<String, String> entry : deviceDataPoints.entrySet()) {
            sb.append(entry.getKey()).append("=");
            sb.append("'").append(entry.getValue()).append("',");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(" WHERE uuid='").append(id).append("';");
        try {
            jdbcTemplate.update(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, String>> selectDeviceTable(String deviceModelName, Map<String, String> deviceDataPoints, String id) {
        return jdbcTemplate.query("SELECT * FROM " + tableName(deviceModelName) + " WHERE uuid='" + id + "';",
                new DeviceDataPointsMapper(deviceDataPoints));
    }

    public List<Map<String, String>> selectDeviceTable(String deviceModelName, Map<String, String> deviceDataPoints, Date date) {
        String formatDate = (date == null) ? "0" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        return jdbcTemplate.query("SELECT * FROM " + tableName(deviceModelName) + " WHERE update_time>'" + formatDate + "';",
                new DeviceDataPointsMapper(deviceDataPoints));
    }

    public void deleteDeviceTable(String deviceModelName, String id) {
        StringBuilder sb = new StringBuilder();
        if (id == null) {
            sb.append("DROP TABLE ").append(tableName(deviceModelName)).append(";");
        } else {
            sb.append("DELETE TABLE ").append(tableName(deviceModelName));
            sb.append(" WHERE uuid=").append(id).append(";");
        }
        try {
            jdbcTemplate.update(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String tableName(String deviceModelName) {
        return "mock_" + deviceModelName + "_tbl";
    }

    private boolean validateTableExist(String deviceModelName) {
        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            ResultSet rs = conn.getMetaData().getTables(null, null, tableName(deviceModelName), null);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    class DeviceDataPointsMapper implements RowMapper<Map<String, String>> {
        private Map<String, String> deviceDataPointsDefine;

        DeviceDataPointsMapper(Map<String, String> deviceDataPointsDefine) {
            this.deviceDataPointsDefine = deviceDataPointsDefine;
        }

        @Override
        public Map<String, String> mapRow(ResultSet resultSet, int i) throws SQLException {
            Map<String, String> deviceDataPoints = new HashMap<>();
            for (Map.Entry<String, String> entry : deviceDataPointsDefine.entrySet()) {
                String deviceDataPoint = entry.getKey();
                deviceDataPoints.put(deviceDataPoint, resultSet.getString(deviceDataPoint));
            }
            return deviceDataPoints;
        }
    }
}
