package wildlog.inaturalist.queryobjects;

import wildlog.inaturalist.queryobjects.enums.INaturalistIgnorePhotos;


/**
 * Used to update observations using [PUT/observations/:id]
 */
public class INaturalistUpdateObservation extends INaturalistAddObservation {
    private long id;
    private String _method = "put";
    private INaturalistIgnorePhotos ignore_photos = INaturalistIgnorePhotos.YES_1;
    
    @Override
    public String getDataString() {
        StringBuilder stringBuilder = new StringBuilder(super.getDataString());
        if (stringBuilder.length() > 0) {
            stringBuilder.append('&');
        }
        if (_method != null && !_method.isEmpty()) {
            stringBuilder.append("_method=").append(_method).append('&');
        }
        if (ignore_photos != null) {
            stringBuilder.append("ignore_photos=").append(ignore_photos).append('&');
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public long getId() {
        return id;
    }

    public void setId(long inId) {
        id = inId;
    }

    public String getMethod() {
        return _method;
    }

    public void setMethod(String inMethod) {
        _method = inMethod;
    }

    public INaturalistIgnorePhotos getIgnore_photos() {
        return ignore_photos;
    }

    public void setIgnore_photos(INaturalistIgnorePhotos inIgnore_photos) {
        ignore_photos = inIgnore_photos;
    }
    
}
