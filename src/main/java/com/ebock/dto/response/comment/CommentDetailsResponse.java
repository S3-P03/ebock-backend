package com.ebock.dto.response.comment;

public class CommentDetailsResponse {
    String firstName;
    String lastName;
    String content;
    Integer idParentComment;
    Integer idComment;
    String timestamp;
    String profilePictureGuid;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getContent() {
        return content;
    }

    public Integer getIdParentComment() {
        return idParentComment;
    }

    public Integer getIdComment() {
        return idComment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getProfilePictureGuid() {
        return profilePictureGuid;
    }
}
