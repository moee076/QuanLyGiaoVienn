import java.sql.Date;

public class GiaoVien {
    private int maGV;
    private String hoTenGV;
    private String khoa;
    private String queQuanGV;
    private Date ngaySinhGV;
    private String sdtGV;
    private String emailGV;
    private String maTK;

    public GiaoVien() {}

    public GiaoVien(int maGV, String hoTenGV, String khoa, String queQuanGV,
                    Date ngaySinhGV, String sdtGV, String emailGV, String maTK) {
        this.maGV = maGV;
        this.hoTenGV = hoTenGV;
        this.khoa = khoa;
        this.queQuanGV = queQuanGV;
        this.ngaySinhGV = ngaySinhGV;
        this.sdtGV = sdtGV;
        this.emailGV = emailGV;
        this.maTK = maTK;
    }

    public int getMaGV() { return maGV; }
    public void setMaGV(int maGV) { this.maGV = maGV; }

    public String getHoTenGV() { return hoTenGV; }
    public void setHoTenGV(String hoTenGV) { this.hoTenGV = hoTenGV; }

    public String getKhoa() { return khoa; }
    public void setKhoa(String khoa) { this.khoa = khoa; }

    public String getQueQuanGV() { return queQuanGV; }
    public void setQueQuanGV(String queQuanGV) { this.queQuanGV = queQuanGV; }

    public Date getNgaySinhGV() { return ngaySinhGV; }
    public void setNgaySinhGV(Date ngaySinhGV) { this.ngaySinhGV = ngaySinhGV; }

    public String getSdtGV() { return sdtGV; }
    public void setSdtGV(String sdtGV) { this.sdtGV = sdtGV; }

    public String getEmailGV() { return emailGV; }
    public void setEmailGV(String emailGV) { this.emailGV = emailGV; }

    public String getMaTK() { return maTK; }
    public void setMaTK(String maTK) { this.maTK = maTK; }
}
