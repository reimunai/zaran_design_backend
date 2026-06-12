package com.example.zaran_design_backend.dto;

/**
 * 用户统计数据 DTO。
 */
public class UserStatisticsResponse {

    private long totalUsers;
    private long adminCount;
    private long inheritorCount;
    private long designerCount;
    private long touristCount;
    private long newUsersToday;
    private long activeUsersWeek;

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getAdminCount() { return adminCount; }
    public void setAdminCount(long adminCount) { this.adminCount = adminCount; }
    public long getInheritorCount() { return inheritorCount; }
    public void setInheritorCount(long inheritorCount) { this.inheritorCount = inheritorCount; }
    public long getDesignerCount() { return designerCount; }
    public void setDesignerCount(long designerCount) { this.designerCount = designerCount; }
    public long getTouristCount() { return touristCount; }
    public void setTouristCount(long touristCount) { this.touristCount = touristCount; }
    public long getNewUsersToday() { return newUsersToday; }
    public void setNewUsersToday(long newUsersToday) { this.newUsersToday = newUsersToday; }
    public long getActiveUsersWeek() { return activeUsersWeek; }
    public void setActiveUsersWeek(long activeUsersWeek) { this.activeUsersWeek = activeUsersWeek; }
}
