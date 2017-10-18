package moravec_tomas.todolist;

public class Item {

    private String name = "";
    private String description = "";
    private String imagePath = "";
    private long date_from = 0;
    private long date_to = 0;

    /*
    long yourmilliseconds = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
    Date resultdate = new Date(yourmilliseconds);
    System.out.println(sdf.format(resultdate));
     */

    @Override
    public String toString() {
        return name;
    }

    public Item(String name){
        this.name = name;
        this.date_from = System.currentTimeMillis();  // Get current time
        this.date_to = System.currentTimeMillis() + 86400000;  // One day from current time
    }

    public Item(String name, String description, String image, long date_from, long date_to){
        this.name = name;
        this.description = description;
        this.imagePath = image;
        this.date_from = date_from;
        this.date_to = date_to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate_from() {
        return date_from;
    }

    public void setDate_from(long date_from) {
        this.date_from = date_from;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getDate_to() {
        return date_to;
    }

    public void setDate_to(long date_to) {
        this.date_to = date_to;
    }
}
