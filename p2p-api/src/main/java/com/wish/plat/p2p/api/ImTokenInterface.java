package com.wish.plat.p2p.api;


import com.alibaba.fastjson.JSONObject;
import com.wish.plat.p2p.api.dto.GetTokenDTO;
import com.wish.plat.p2p.api.dto.SendMsgToImDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

/**
 * @author yxd
 */
@Path("/token")
@Consumes("application/json;charset=UTF-8")
@Produces("application/json;charset=UTF-8")
public interface ImTokenInterface {

    @POST
    @Path("/getToken.json")
    JSONObject getToken(GetTokenDTO getTokenDTO);

    @POST
    @Path("/getToken")
    Map getToken(Map getTokenMap);
}
