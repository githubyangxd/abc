package com.wish.plat.p2p.api;


import com.wish.plat.p2p.api.dto.GetTokenDTO;
import com.wish.plat.p2p.api.dto.SendMsgToImDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @author yxd
 */
@Path("/toIm")
@Consumes("application/json;charset=UTF-8")
@Produces("application/json;charset=UTF-8")
public interface SendMsgToImInterface {

//    @POST
//    @Path("/sendMsgToIm")
//    String sendMsgToIm(String requestBody);

    @POST
    @Path("/sendMsg")
    String sendMsg(SendMsgToImDTO sendMsgToImDTO);

}
