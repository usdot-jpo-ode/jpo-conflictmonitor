package us.dot.its.jpo.conflictmonitor.monitor.mongo;

public class MongoIndexPojo {
    private String name;
    private String timefield;
    private Integer expiretime;


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimefield() {
        return this.timefield;
    }

    public void setTimefield(String timefield) {
        this.timefield = timefield;
    }

    public Integer getExpiretime() {
        return this.expiretime;
    }

    public void setExpiretime(Integer expiretime) {
        this.expiretime = expiretime;
    }

    @Override
    public String toString() {
        return "{" +
            " name='" + getName() + "'" +
            ", timefield='" + getTimefield() + "'" +
            ", expiretime='" + getExpiretime() + "'" +
            "}";
    }

}
