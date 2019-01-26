package wildlog.data.dataobjects.interfaces;

// FIXME: Die Comparable op die vlak is bietjie lelik, maar vir nou werk dit so... Sou graag 'n Generic Type wou gebruik...
public interface DataObjectBasicInfo extends Comparable {
    public String getExportPrefix();
    public String getDisplayName();
    public String getIDField();
}
