package tech.info.sasurie.sociall;

public class DataSetFire {
    String fullname, mark, profileimage, testname, testtopicname, date,count;
    int totalmark;

    public DataSetFire()
    {

    }

    public DataSetFire(String fullname, String mark, String profileimage, String testname, String testtopicname, String date,String count, int totalmark) {
        this.fullname = fullname;
        this.mark = mark;
        this.profileimage = profileimage;
        this.testname = testname;
        this.testtopicname = testtopicname;
        this.date = date;
        this.totalmark = totalmark;
        this.count =count;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }

    public String getTesttopicname() {
        return testtopicname;
    }

    public void setTesttopicname(String testtopicname) {
        this.testtopicname = testtopicname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public int getTotalmark() {
        return totalmark;
    }

    public void setTotalmark(int totalmark) {
        this.totalmark = totalmark;
    }
}