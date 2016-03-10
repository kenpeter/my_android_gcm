package kenpeter.kenpeter.mygcm.app;

public class EndPoints {

    // localhost url -
    // Need full ip
    //public static final String BASE_URL = "http://192.168.122.1/test/testme/chatapp/v1";
    public static final String BASE_URL = "http://192.168.0.12:8000/v1";
    public static final String LOGIN = BASE_URL + "/user/login";
    public static final String USER = BASE_URL + "/user/_ID_";
    public static final String CHAT_ROOMS = BASE_URL + "/chat_rooms";
    public static final String CHAT_THREAD = BASE_URL + "/chat_rooms/_ID_";
    public static final String CHAT_ROOM_MESSAGE = BASE_URL + "/chat_rooms/_ID_/message";
}