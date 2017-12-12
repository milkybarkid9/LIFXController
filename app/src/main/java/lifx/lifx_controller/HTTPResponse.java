package lifx.lifx_controller;

public class HTTPResponse {
    private int returnCode;
    private String returnResponse;

    HTTPResponse(int code, String response){
        returnCode=code;
        returnResponse=response;
    }

    public int getCode(){
        return returnCode;
    }

    public String getReponse(){
        return returnResponse;
    }
}
