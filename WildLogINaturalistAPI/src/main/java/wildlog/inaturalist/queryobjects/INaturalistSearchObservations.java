package wildlog.inaturalist.queryobjects;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import wildlog.inaturalist.queryobjects.enums.INaturalistExtra;
import wildlog.inaturalist.queryobjects.enums.INaturalistHas;
import wildlog.inaturalist.queryobjects.enums.INaturalistIconicTaxa;
import wildlog.inaturalist.queryobjects.enums.INaturalistLicense;
import wildlog.inaturalist.queryobjects.enums.INaturalistOn;
import wildlog.inaturalist.queryobjects.enums.INaturalistOrder;
import wildlog.inaturalist.queryobjects.enums.INaturalistOrderBy;
import wildlog.inaturalist.queryobjects.enums.INaturalistQualityGrade;
import wildlog.inaturalist.utils.UtilsINaturalist;


/**
 * Used to for [GET/observations]
 */
public class INaturalistSearchObservations {
    private static final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ZONED_DATE_TIME_FORMAT = DateTimeFormatter.ISO_DATE_TIME;
    private String q;
    private int page;
    private int per_page;
    private INaturalistOrderBy order_by;
    private INaturalistOrder order;
    private INaturalistLicense license;
    private INaturalistLicense photo_license;
    private int taxon_id;
    private String taxon_name;
    private List<INaturalistIconicTaxa> iconic_taxa;
    private List<INaturalistHas> has;
    private INaturalistQualityGrade quality_grade;
    private boolean out_of_range;
    private INaturalistOn on;
    private int year;
    private int month;
    private int day;
    private LocalDate d1;
    private LocalDate d2;
    private int m1;
    private int m2;
    private int h1;
    private int h2;
    private double swlat;
    private double swlng;
    private double nelat;
    private double nelng;
    private int list_id;
    private ZonedDateTime updated_since;
    private INaturalistExtra extra;
    
    public String getQueryString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('?');
        if (q != null && !q.isEmpty()) {
            stringBuilder.append("q=").append(UtilsINaturalist.forURL(q)).append('&');
        }
        if (page >= 0) {
            stringBuilder.append("page=").append(page).append('&');
        }
        if (per_page >= 1 && per_page <= 200) {
            stringBuilder.append("per_page=").append(per_page).append('&');
        }
        if (order_by != null) {
            stringBuilder.append("order_by=").append(UtilsINaturalist.forURL(order_by)).append('&');
        }
        if (order != null) {
            stringBuilder.append("order=").append(UtilsINaturalist.forURL(order)).append('&');
        }
        if (license != null) {
            stringBuilder.append("license=").append(UtilsINaturalist.forURL(license)).append('&');
        }
        if (photo_license != null) {
            stringBuilder.append("photo_license=").append(UtilsINaturalist.forURL(photo_license)).append('&');
        }
        if (taxon_id > 0) {
            stringBuilder.append("taxon_id=").append(taxon_id).append('&');
        }
        if (taxon_name != null && !taxon_name.isEmpty()) {
            stringBuilder.append("taxon_name=").append(UtilsINaturalist.forURL(taxon_name)).append('&');
        }
        if (iconic_taxa != null && !iconic_taxa.isEmpty()) {
            for (INaturalistIconicTaxa iconicTaxa : iconic_taxa) {
                stringBuilder.append("iconic_taxa[]=").append(UtilsINaturalist.forURL(iconicTaxa)).append('&');
            }
        }
        if (has != null && !has.isEmpty()) {
            for (INaturalistHas has : has) {
                stringBuilder.append("has[]=").append(UtilsINaturalist.forURL(has)).append('&');
            }
        }
        if (quality_grade != null) {
            stringBuilder.append("quality_grade=").append(UtilsINaturalist.forURL(quality_grade)).append('&');
        }
        if (out_of_range) {
            stringBuilder.append("out_of_range=").append(out_of_range).append('&');
        }
        if (on != null) {
            stringBuilder.append("on=").append(UtilsINaturalist.forURL(on.getAsINatString())).append('&');
        }
        if (year > 0) {
            stringBuilder.append("year=").append(year).append('&');
        }
        if (month >= 1 && month <= 12) {
            stringBuilder.append("month=").append(month).append('&');
        }
        if (day >= 1 && day <= 31) {
            stringBuilder.append("day=").append(day).append('&');
        }
        if (d1 != null) {
            stringBuilder.append("d1=").append(UtilsINaturalist.forURL(LOCAL_DATE_FORMAT.format(d1))).append('&');
        }
        if (d2 != null) {
            stringBuilder.append("d2=").append(UtilsINaturalist.forURL(LOCAL_DATE_FORMAT.format(d2))).append('&');
        }
        if (m1 >= 1 && m1 <= 12) {
            stringBuilder.append("m1=").append(m1).append('&');
        }
        if (m2 >= 1 && m2 <= 12) {
            stringBuilder.append("m2=").append(m2).append('&');
        }
        if (h1 >= 1 && h1 <= 31) {
            stringBuilder.append("h1=").append(h1).append('&');
        }
        if (h2 >= 1 && h2 <= 31) {
            stringBuilder.append("h2=").append(h2).append('&');
        }
        if (swlat >= -90 && swlat <= 90 && swlat != 0) {
            stringBuilder.append("swlat=").append(UtilsINaturalist.forURL(swlat)).append('&');
        }
        if (swlng >= -180 && swlng <= 180 && swlng != 0) {
            stringBuilder.append("swlng=").append(UtilsINaturalist.forURL(swlng)).append('&');
        }
        if (nelat >= -90 && nelat <= 90 && nelat != 0) {
            stringBuilder.append("nelat=").append(UtilsINaturalist.forURL(nelat)).append('&');
        }
        if (nelng >= -180 && nelng <= 180 && nelng != 0) {
            stringBuilder.append("nelng=").append(UtilsINaturalist.forURL(nelng)).append('&');
        }
        if (list_id > 0) {
            stringBuilder.append("list_id=").append(list_id).append('&');
        }
        if (updated_since != null) {
            stringBuilder.append("d2=").append(UtilsINaturalist.forURL(ZONED_DATE_TIME_FORMAT.format(updated_since))).append('&');
        }
        if (extra != null) {
            stringBuilder.append("extra=").append(UtilsINaturalist.forURL(extra)).append('&');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public String getQ() {
        return q;
    }

    /**
     * <b>Note: Rather use the specific fields.</b> <br/>
     * Intended to be used alone. 
     * Can be any String to search for.
     */
    public void setQ(String q) {
        this.q = q;
    }

    public int getPage() {
        return page;
    }

    /**
     * Any positive integer.
     */
    public void setPage(int page) {
        this.page = page;
    }

    public int getPer_page() {
        return per_page;
    }

    /**
     * Must be between 1 to 200.
     */
    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public INaturalistOrderBy getOrder_by() {
        return order_by;
    }

    public void setOrder_by(INaturalistOrderBy order_by) {
        this.order_by = order_by;
    }

    public INaturalistOrder getOrder() {
        return order;
    }

    public void setOrder(INaturalistOrder order) {
        this.order = order;
    }

    public INaturalistLicense getLicense() {
        return license;
    }

    public void setLicense(INaturalistLicense license) {
        this.license = license;
    }

    public INaturalistLicense getPhoto_license() {
        return photo_license;
    }

    public void setPhoto_license(INaturalistLicense photo_license) {
        this.photo_license = photo_license;
    }

    public int getTaxon_id() {
        return taxon_id;
    }

    /**
     * The iNaturalist Taxon ID.
     */
    public void setTaxon_id(int taxon_id) {
        this.taxon_id = taxon_id;
    }

    public String getTaxon_name() {
        return taxon_name;
    }

    /**
     * The Scientific Name (or other taxonomic category).
     */
    public void setTaxon_name(String taxon_name) {
        this.taxon_name = taxon_name;
    }

    public List<INaturalistIconicTaxa> getIconic_taxa() {
        return iconic_taxa;
    }

    public void setIconic_taxa(List<INaturalistIconicTaxa> iconic_taxa) {
        this.iconic_taxa = iconic_taxa;
    }

    public List<INaturalistHas> getHas() {
        return has;
    }

    public void setHas(List<INaturalistHas> has) {
        this.has = has;
    }

    public INaturalistQualityGrade getQuality_grade() {
        return quality_grade;
    }

    public void setQuality_grade(INaturalistQualityGrade quality_grade) {
        this.quality_grade = quality_grade;
    }

    public boolean isOut_of_range() {
        return out_of_range;
    }

    /**
     * Filter by whether or not iNat considers the observation out of range for the associated taxon. 
     * This is based on iNat's range data.
     */
    public void setOut_of_range(boolean out_of_range) {
        this.out_of_range = out_of_range;
    }

    public INaturalistOn getOn() {
        return on;
    }

    public void setOn(INaturalistOn on) {
        this.on = on;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    /**
     * Between 1 to 12.
     */
    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    /**
     * Between 1 to 31.
     */
    public void setDay(int day) {
        this.day = day;
    }

    public LocalDate getD1() {
        return d1;
    }

    /**
     * First date of an interval. 
     */
    public void setD1(LocalDate d1) {
        this.d1 = d1;
    }

    public LocalDate getD2() {
        return d2;
    }

    /**
     * Second date of an interval. 
     */
    public void setD2(LocalDate d2) {
        this.d2 = d2;
    }

    public int getM1() {
        return m1;
    }

    /**
     * First month of an interval. 
     * Between 1 to 12;
     */
    public void setM1(int m1) {
        this.m1 = m1;
    }

    public int getM2() {
        return m2;
    }

    /**
     * Second month of an interval. 
     * Between 1 to 12;
     */
    public void setM2(int m2) {
        this.m2 = m2;
    }

    public int getH1() {
        return h1;
    }

    /**
     * First hour of an interval. 
     * Between 0 to 23;
     */
    public void setH1(int h1) {
        this.h1 = h1;
    }

    public int getH2() {
        return h2;
    }

    /**
     * Second hour of an interval. 
     * Between 0 to 23;
     */
    public void setH2(int h2) {
        this.h2 = h2;
    }

    public double getSwlat() {
        return swlat;
    }

    /**
     * Southwest latitude of a bounding box query. 
     * Between -90 to 90;
     */
    public void setSwlat(double swlat) {
        this.swlat = swlat;
    }

    public double getSwlng() {
        return swlng;
    }

    /**
     * Southwest longitude of a bounding box query. 
     * Between -180 to 180;
     */
    public void setSwlng(double swlng) {
        this.swlng = swlng;
    }

    public double getNelat() {
        return nelat;
    }

    /**
     * Northeast latitude of a bounding box query. 
     * Between -90 to 90;
     */
    public void setNelat(double nelat) {
        this.nelat = nelat;
    }

    public double getNelng() {
        return nelng;
    }

    /**
     * Northeast longitude of a bounding box query. 
     * Between -180 to 180;
     */
    public void setNelng(double nelng) {
        this.nelng = nelng;
    }

    public int getList_id() {
        return list_id;
    }

    /**
     * Restrict results to observations of taxa on the specified list. Limited to lists with 2000 taxa or less.
     */
    public void setList_id(int list_id) {
        this.list_id = list_id;
    }

    public ZonedDateTime getUpdated_since() {
        return updated_since;
    }

    public void setUpdated_since(ZonedDateTime updated_since) {
        this.updated_since = updated_since;
    }

    public INaturalistExtra getExtra() {
        return extra;
    }

    public void setExtra(INaturalistExtra extra) {
        this.extra = extra;
    }
    
}
