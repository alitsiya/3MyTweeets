package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.utils.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the DBFlow wiki for more details:
 * https://github.com/codepath/android_guides/wiki/DBFlow-Guide
 *
 * Note: All models **must extend from** `BaseModel` as shown below.
 * 
 */
@Table(database = MyDatabase.class)
public class TweetModel extends BaseModel {

	@PrimaryKey
	@Column Long id;

	// Tweet
	@Column private String body;
	@Column private long uid;
	@Column private String createdAt;
	// User
	@Column private String userName;
	@Column private long userUid;
	@Column private String screenName;
	@Column private String profileImageUrl;

	public TweetModel() {
		super();
	}

	// Parse model from JSON
	public TweetModel(JSONObject jsonObject){
		super();

		try {
			this.body = jsonObject.getString("text");
			this.uid = jsonObject.getLong("id");
			this.createdAt = jsonObject.getString("created_at");
			User user = User.fromJSON(jsonObject.getJSONObject("user"));
			this.userName = user.name;
			this.userUid = user.uid;
			this.screenName = user.screenName;
			this.profileImageUrl = user.profileImageUrl;
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// Getters
	public String getBody() {
		return body;
	}
	public long getUid() {
		return uid;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public String getUserName() {
		return userName;
	}
	public long getUserUid() {
		return userUid;
	}
	public String getScreenName() {
		return screenName;
	}
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	// Setters
	public void setBody(String body) {
		this.body = body;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setUserUid(long userUid) {
		this.userUid = userUid;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	/* The where class in this code below will be marked red until you first compile the project, since the code 
	 * for the SampleModel_Table class is generated at compile-time.
	 */
	
	// Record Finders
	public static TweetModel byId(long id) {
		return new Select().from(TweetModel.class).where(TweetModel_Table.uid.eq(id)).querySingle();
	}

	public static List<TweetModel> orderByDate() {
		return new Select().from(TweetModel.class).orderBy(OrderBy.fromProperty(TweetModel_Table.createdAt)).queryList();
	}

	public static List<TweetModel> recentItems() {
		return new Select().from(TweetModel.class).orderBy(TweetModel_Table.id, false).limit(300).queryList();
	}
}
