

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class QueryTeacher
 */
@WebServlet("/QueryTeacher")
public class QueryTeacher extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryTeacher() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = new DBConnection();
		PrintWriter out = response.getWriter();
		String type = request.getParameter("type");
		Cookie[] cookies = request.getCookies();
		JSONObject result = new JSONObject();
		JSONArray array = new JSONArray();
		String userid = "";
		for (Cookie cookie: cookies){
			if(cookie.getName().equals("id")){
				userid = cookie.getValue();
			}
		}
		
		try {
			Statement statement = connection.getConnection().createStatement();
			String query;
			if (type.equals("course")){
				query = "SELECT * FROM offer,course WHERE offer.course_id=course.course_id AND offer.teacher_id='" + userid + "'";
				ResultSet rs = statement.executeQuery(query);
				while (rs.next()){
					JSONObject item = new JSONObject();
					item.put("course_id", rs.getString("course_id"));
					item.put("course_name", rs.getString("course_name"));
					item.put("total_time", rs.getString("total_time"));
					array.put(item);
				}
				result.put("courses", array);
			}
			else if (type.equals("upload")){//course_id
				String id = request.getParameter("course_id");
				query = "SELECT * FROM attend,offer WHERE attend.course_id=offer.course_id AND course_id='" + id + "' AND teacher_id='" + userid + "'";
				ResultSet rs = statement.executeQuery(query);
				boolean success = false;
				while (rs.next()) {
					success = true;
					if (rs.getInt("exam_times") == 0){
						JSONObject item = new JSONObject();
						item.put("employee_id", rs.getString("employee_id"));
						array.put(item);
					}
				}
				if (success) {
					result.put("result", "1");
					result.put("users", array);
				}
				else {
					result.put("result", "0");
					result.put("message", "No one choose this course or this is not your course");
				}
			}
			else if (type.equals("update")){
				String id = request.getParameter("course_id");
				query = "SELECT * FROM attend,offer WHERE attend.course_id=offer.course_id AND course_id='" + id + "' AND teacher_id='" + userid + "'";
				ResultSet rs = statement.executeQuery(query);
				boolean success = false;
				while (rs.next()) {
					success = true;
					if (rs.getInt("exam_times") == 1 && rs.getBoolean("permit_retest")){
						JSONObject item = new JSONObject();
						item.put("employee_id", rs.getString("employee_id"));
						array.put(item);
					}
				}
				if (success) {
					result.put("result", "1");
					result.put("users", array);
				}
				else {
					result.put("result", "0");
					result.put("message", "No one to update or this is not your course");
				}
			}
            else if (type.equals("retest")){
            	String id = request.getParameter("course_id");
				query = "SELECT * FROM attend,offer WHERE attend.course_id=offer.course_id AND course_id='" + id + "' AND teacher_id='" + userid + "'";
				ResultSet rs = statement.executeQuery(query);
				boolean success = false;
				while (rs.next()) {
					success = true;
					if (rs.getInt("exam_times") == 1 && !rs.getBoolean("permit_retest") && rs.getBoolean("apply_retest")){
						JSONObject item = new JSONObject();
						item.put("employee_id", rs.getString("employee_id"));
						array.put(item);
					}
				}
				if (success) {
					result.put("result", "1");
					result.put("users", array);
				}
				else {
					result.put("result", "0");
					result.put("message", "No one to update or this is not your course");
				}
			}
			out.print(result);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
