package wildlog.inaturalist.queryobjects;


/**
 * Used to for [PUT/observations/:id]
 */
public class INaturalistUpdateObservation extends INaturalistAddObservation {
    private String _method = "put";
    private int ignore_photos;
    
    public String getDataString() {
        StringBuilder stringBuilder = new StringBuilder(super.getDataString());
        if (stringBuilder.length() > 0) {
            stringBuilder.append('&');
        }
        if (_method != null && !_method.isEmpty()) {
            stringBuilder.append("observation[_method]=").append(_method).append('&');
        }
        if (ignore_photos == 0 || ignore_photos == 1) {
            stringBuilder.append("observation[ignore_photos]=").append(ignore_photos).append('&');
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public String getMethod() {
        return _method;
    }

    public void setMethod(String _method) {
        this._method = _method;
    }

    public int getIgnore_photos() {
        return ignore_photos;
    }

    public void setIgnore_photos(int ignore_photos) {
        this.ignore_photos = ignore_photos;
    }
    
}
