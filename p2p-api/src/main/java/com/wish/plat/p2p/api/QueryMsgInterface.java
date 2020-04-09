package com.wish.plat.p2p.api;

import com.wish.plat.p2p.api.dto.GetTokenDTO;
import com.wish.plat.p2p.api.dto.QueryPushMsgDTO;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

/**
 * @author yxd
 */
@Path("/query")
@Consumes("application/json;charset=UTF-8")
@Produces("application/json;charset=UTF-8")
public interface QueryMsgInterface {

    @POST
    @Path("/tellerMsg")
    String queryTellerMsg(String queryInfo);

    @POST
    @Path("/historyMsg")
    String queryTellerMsg(QueryPushMsgDTO queryPushMsgDTO);

    @POST
    @Path("/selectMsg")
    Map queryTellerMsg(Map selectMap);
}
