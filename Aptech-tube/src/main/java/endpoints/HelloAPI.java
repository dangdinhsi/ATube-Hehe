package endpoints;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;
import entity.ATube;
import entity.JsonResponse;
import util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class HelloAPI extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ATube.class.getName());
    private static Gson gson = new Gson();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        List<ATube> list = ofy().load().type(ATube.class).filter("status",1).list();
        JsonResponse jsonResponse = new JsonResponse(HttpServletResponse.SC_OK,"Get list ok",list);
        resp.getWriter().println(gson.toJson(jsonResponse));
}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try{
        String content = StringUtil.convertInputStreamToString(req.getInputStream());
        ATube aTube = gson.fromJson(content,ATube.class);
        Key<ATube> aTubeKey=ofy().save().entity(aTube).now();
        resp.setStatus(HttpServletResponse.SC_CREATED);
        JsonResponse jsonResponse = new JsonResponse(HttpServletResponse.SC_CREATED,"Lưu thành công!!!",aTubeKey);
        resp.getWriter().println(gson.toJson(jsonResponse));
        }catch (Exception ex){
        String messageError =String.format("Cannot create ATube,error: %s",ex.getMessage());
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        JsonResponse jsonResponse = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,messageError,null);
        resp.getWriter().println(gson.toJson(jsonResponse));
        LOGGER.log(Level.SEVERE,messageError);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // kiểm tra tồn tại của id trong parameter, nếu không tòn tại trả về bad request;
        String strId = req.getParameter("id");
        if(strId==null||strId.length()==0){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonResponse jsonResponse =new JsonResponse(HttpServletResponse.SC_BAD_REQUEST,"Id không thể bỏ trống, vui lòng nhập lại!!!",null);
            resp.getWriter().println(gson.toJson(jsonResponse));
            LOGGER.log(Level.SEVERE,"400-BAD REQUEST");
            return;
        }
        // Kiểm tra sự tồn tại của Atube trong databse với id truyền lên, nếu không tồn tại thì trả về not found
        ATube existAtube = ofy().load().type(ATube.class).id(Long.parseLong(strId)).now();
        if(existAtube==null||existAtube.getStatus()==0){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonResponse jsonResponse = new JsonResponse(HttpServletResponse.SC_NOT_FOUND,"ATube có id này không tồn tại hoặc đã bị xóa!!!",null);
            resp.getWriter().println(gson.toJson(jsonResponse));
            LOGGER.log(Level.SEVERE,"404-NOT FOUND");
            return;
        }
        try {
            String content = StringUtil.convertInputStreamToString(req.getInputStream());
            ATube updateAtube = gson.fromJson(content,ATube.class);
            existAtube.setName(updateAtube.getName());
            existAtube.setDescription(updateAtube.getDescription());
            existAtube.setUpdatedAt(Calendar.getInstance().getTime());
            ofy().save().entity(existAtube).now();
            resp.setStatus(HttpServletResponse.SC_OK);
            JsonResponse jsonResponse = new JsonResponse(HttpServletResponse.SC_OK,"Update thành công!!!",existAtube);
            resp.getWriter().println(gson.toJson(jsonResponse));
        }catch (Exception ex){
            String messageError = String.format("Cannot update ATube, error: %s", ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonResponse jsonResponse = new JsonResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,messageError,null);
            resp.getWriter().println(gson.toJson(jsonResponse));
            LOGGER.log(Level.SEVERE, messageError);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        // kiểm tra tồn tại tham số id trong parameter (lưu ý, đây là cách tạm thời)
        // trong trường hợp không tồn tại thì trả về bad request.
        String strId = req.getParameter("id");
        if (strId == null || strId.length() == 0) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonResponse jsonResponse = new JsonResponse(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "id không thể bỏ trống, vui lòng nhập lại!!!",
                    null);
            resp.getWriter().println(gson.toJson(jsonResponse));
            LOGGER.log(Level.SEVERE, "400-BAD REQUEST");
            return;
        }
        ATube existTube = ofy().load().type(ATube.class).id(Long.parseLong(strId)).now();
        if (existTube == null || existTube.getStatus() == 0) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            JsonResponse jsonResponse = new JsonResponse(
                    HttpServletResponse.SC_NOT_FOUND,
                    "ATube có id này không tồn tại hoặc đã bị xóa!!!",
                    null);
            resp.getWriter().println(gson.toJson(jsonResponse));
            LOGGER.log(Level.SEVERE, "404-NOT_FOUND");
            return;
        }
        try {
            existTube.setStatus(ATube.Status.delete);
            existTube.setDeletedAt(Calendar.getInstance().getTime());
            ofy().save().entity(existTube).now();

            resp.setStatus(HttpServletResponse.SC_OK);
            JsonResponse jsonResponse = new JsonResponse(
                    HttpServletResponse.SC_OK,
                    "Xóa thành công!!!",
                    null);
            resp.getWriter().println(gson.toJson(jsonResponse));
        } catch (Exception ex) {
            String messageError = String.format("Cannot remove ATube, error: %s", ex.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonResponse jsonResponse = new JsonResponse(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    messageError,
                    null);
            resp.getWriter().println(gson.toJson(jsonResponse));
            LOGGER.log(Level.SEVERE, messageError);
        }

    }
}
