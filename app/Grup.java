import java.sql.Timestamp;
import java.util.ArrayList;

public class Grup {
    private String grupId;
    private String grupAdi;
    private String olusturanID;
    private Timestamp olusturulmaTarihi;
    private ArrayList<String> uyeler;

    public Grup(String grupId, String grupAdi, String olusturanID, Timestamp olusturulmaTarihi, ArrayList<String> uyeler) {
        this.grupId = grupId;
        this.grupAdi = grupAdi;
        this.olusturanID = olusturanID;
        this.olusturulmaTarihi = olusturulmaTarihi;
        this.uyeler = uyeler;
    }
    public String getGrupId() {
        return grupId;
    }
    public void setGrupId(String grupId) {
        this.grupId = grupId;
    }

    public String getGrupAdi() {
        return grupAdi;
    }

    public void setGrupAdi(String grupAdi) {
        this.grupAdi = grupAdi;
    }

    public String getOlusturanID() {
        return olusturanID;
    }

    public void setOlusturanID(String olusturanID) {
        this.olusturanID = olusturanID;
    }

    public Timestamp getOlusturulmaTarihi() {
        return olusturulmaTarihi;
    }

    public void setOlusturulmaTarihi(Timestamp olusturulmaTarihi) {
        this.olusturulmaTarihi = olusturulmaTarihi;
    }

    public ArrayList<String> getUyeler() {
        return uyeler;
    }

    public void setUyeler(ArrayList<String> uyeler) {
        this.uyeler = uyeler;
    }
}
