import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GiaoVienModify {

    // SỬA nếu DB bạn khác tên
    private static final String URL =
        "jdbc:mysql://localhost:3306/ql_giaovien?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASS = "";

    private static Connection getConnection() throws SQLException {
        try {
            // cần MySQL Connector/J
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Thiếu MySQL Connector/J. NetBeans > Libraries > Add JAR/Folder > mysql-connector-j-*.jar", e
            );
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    private static GiaoVien map(ResultSet rs) throws SQLException {
        return new GiaoVien(
            rs.getInt("maGV"),
            rs.getString("hoTenGV"),
            rs.getString("khoa"),
            rs.getString("queQuanGV"),
            rs.getDate("ngaySinhGV"),
            rs.getString("sdtGV"),
            rs.getString("emailGV"),
            rs.getString("maTK")
        );
    }

    public static List<GiaoVien> findAll() {
        String sql = "SELECT maGV, hoTenGV, khoa, queQuanGV, ngaySinhGV, sdtGV, emailGV, maTK FROM giaovien ORDER BY maGV ASC";
        List<GiaoVien> list = new ArrayList<>();

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("findAll lỗi: " + e.getMessage(), e);
        }
    }

    // Insert trả về maGV vừa tạo (auto_increment)
    public static int insert(GiaoVien gv) {
        String sql = "INSERT INTO giaovien(hoTenGV, khoa, queQuanGV, ngaySinhGV, sdtGV, emailGV, maTK) VALUES(?,?,?,?,?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, gv.getHoTenGV());
            ps.setString(2, gv.getKhoa());
            ps.setString(3, emptyToNull(gv.getQueQuanGV()));
            ps.setDate(4, gv.getNgaySinhGV()); // có thể null
            ps.setString(5, emptyToNull(gv.getSdtGV()));
            ps.setString(6, emptyToNull(gv.getEmailGV()));
            ps.setString(7, emptyToNull(gv.getMaTK()));

            int affected = ps.executeUpdate();
            if (affected == 0) throw new SQLException("Insert thất bại, không có dòng nào được thêm.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;

        } catch (SQLException e) {
            throw new RuntimeException("insert lỗi: " + e.getMessage(), e);
        }
    }

    public static boolean update(GiaoVien gv) {
        String sql = """
            UPDATE giaovien
            SET hoTenGV=?, khoa=?, queQuanGV=?, ngaySinhGV=?, sdtGV=?, emailGV=?, maTK=?
            WHERE maGV=?
        """;

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, gv.getHoTenGV());
            ps.setString(2, gv.getKhoa());
            ps.setString(3, emptyToNull(gv.getQueQuanGV()));
            ps.setDate(4, gv.getNgaySinhGV());
            ps.setString(5, emptyToNull(gv.getSdtGV()));
            ps.setString(6, emptyToNull(gv.getEmailGV()));
            ps.setString(7, emptyToNull(gv.getMaTK()));
            ps.setInt(8, gv.getMaGV());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("update lỗi: " + e.getMessage(), e);
        }
    }

    public static boolean delete(int maGV) {
        String sql = "DELETE FROM giaovien WHERE maGV=?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, maGV);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("delete lỗi: " + e.getMessage(), e);
        }
    }

    // Tìm: nếu keyword là số -> maGV; nếu yyyy-MM-dd -> ngaySinhGV; còn lại -> LIKE text
    public static List<GiaoVien> find(String keyword) {
        String k = (keyword == null) ? "" : keyword.trim();
        List<GiaoVien> list = new ArrayList<>();
        if (k.isEmpty()) return list;

        boolean isNumber = k.matches("\\d+");
        Date d = parseDateOrNull(k);

        String sqlById = "SELECT * FROM giaovien WHERE maGV=?";
        String sqlByDate = "SELECT * FROM giaovien WHERE ngaySinhGV=?";
        String sqlLike = """
            SELECT * FROM giaovien
            WHERE hoTenGV LIKE ?
               OR khoa LIKE ?
               OR queQuanGV LIKE ?
               OR sdtGV LIKE ?
               OR emailGV LIKE ?
               OR maTK LIKE ?
            ORDER BY maGV ASC
        """;

        try (Connection c = getConnection()) {
            PreparedStatement ps;

            if (isNumber) {
                ps = c.prepareStatement(sqlById);
                ps.setInt(1, Integer.parseInt(k));
            } else if (d != null) {
                ps = c.prepareStatement(sqlByDate);
                ps.setDate(1, d);
            } else {
                ps = c.prepareStatement(sqlLike);
                String like = "%" + k + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
                ps.setString(4, like);
                ps.setString(5, like);
                ps.setString(6, like);
            }

            try (ps; ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
            return list;

        } catch (SQLException e) {
            throw new RuntimeException("find lỗi: " + e.getMessage(), e);
        }
    }

    public static Date parseDateOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return Date.valueOf(t); // yyyy-MM-dd
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
