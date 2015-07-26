package com.yingshi.toutiao.util;

public class ServerMock {
	/*
	private final static String tag = "TT-ServerMock";
	final static String body = "android:onClick Name of the method in this View's context to invoke when the view is clicked. This name must correspond to a public method that takes exactly one parameter of type View. For instance, if you specify android:onClick=\"sayHello\", you must declare a public void sayHello(View v) method of your context (typically, your Activity)" + 
                               "Must be a string value, using '\\;' to escape characters such as '\\n' or '\\uxxxx' for a unicode character. This may also be a reference to a resource (in the form \"@[package:]type:name\") or theme attribute (in the form \"?[package:][type:]name\") containing a value of this type. This corresponds to the global attribute resource symbol onClick.";
	public static NewsPage getNewsPage(String category){
		Log.d(tag, "getNewsPage " + category);
		NewsPage news = new NewsPage();
		if(TempCategory.HEADLINE.name() == category){
			for(int i=1; i <= 4; i++){
				Photo photo = new Photo();
				photo.setUrl("HEADLINE"+i);
				photo.setDescription("This is the headline "+i);
				news.getPhotos().add(photo);
			}
			
			for(int i=1; i<=10; i++){
				Article article = new Article();
				if(i%3 == 0)
					article.setType(Type.SPECIAL);
				else if( i%2 == 0)
					article.setType(Type.VIDEO);
				else
					article.setType(Type.TEXT);
				article.setTitle("HEADLINE" + i);
				Photo photo = new Photo();
				photo.setUrl("HEADLINE" + ( i%4 + 1) );
				article.setCreateTime(new Date().getTime());
				article.setPhoto(photo);
				article.setContent(body);
				news.getArticles().add(article);
			}
			
		}else if(TempCategory.MOVIE.name() == category){
			for(int i=1; i <= 3; i++){
				Photo photo = new Photo();
				photo.setUrl("MOVIE"+i);
				photo.setDescription("This is the movie "+i);
				news.getPhotos().add(photo);
			}
			
			for(int i=1; i<=13; i++){
				Article article = new Article();
				if(i%3 == 0)
					article.setType(Type.SPECIAL);
				else if( i%2 == 0)
					article.setType(Type.VIDEO);
				else
					article.setType(Type.TEXT);
				article.setTitle("MOVIE" + i);
				article.setCreateTime(new Date().getTime());
				Photo photo = new Photo();
				photo.setUrl("MOVIE" + ( i%3 + 1) );
				article.setPhoto(photo);
				article.setContent(body);
				news.getArticles().add(article);
			}
		}else if(TempCategory.RATINGS.name() == category){
			for(int i=1; i <= 3; i++){
				Photo photo = new Photo();
				photo.setUrl("RATINGS"+i);
				photo.setDescription("This is the rating "+i);
				news.getPhotos().add(photo);
			}
			
			for(int i=1; i<=6; i++){
				Article article = new Article();
				if(i%3 == 0)
					article.setType(Type.SPECIAL);
				else if( i%2 == 0)
					article.setType(Type.VIDEO);
				else
					article.setType(Type.TEXT);
				article.setTitle("RATINGS" + i);
				Photo photo = new Photo();
				photo.setUrl("RATINGS" + ( i%3 + 1) );
				article.setPhoto(photo);
				article.setCreateTime(new Date().getTime());
				article.setContent(body);
				news.getArticles().add(article);
			}
		}else if(TempCategory.TELEPLAY.name() == category){
			for(int i=1; i <= 3; i++){
				Photo photo = new Photo();
				photo.setUrl("TELEPLAY"+i);
				photo.setDescription("This is the teleplay "+i);
				news.getPhotos().add(photo);
			}
			
			for(int i=1; i<=16; i++){
				Article article = new Article();
				if(i%3 == 0)
					article.setType(Type.SPECIAL);
				else if( i%2 == 0)
					article.setType(Type.VIDEO);
				else
					article.setType(Type.TEXT);
				article.setTitle("TELEPLAY" + i);
				Photo photo = new Photo();
				photo.setUrl("TELEPLAY" + ( i%3 + 1) );
				article.setPhoto(photo);
				article.setCreateTime(new Date().getTime());
				article.setContent(body);
				news.getArticles().add(article);
			}
		}
		return news;
	}
	

	public static Bitmap getPhoto(String url, Context ctx){
		Log.d(tag, "getPhoto " + url);
		int resId = 0;
		if(url.equals("HEADLINE1")){
			resId = R.drawable.headline1;
		}else if(url.equals("HEADLINE2")){
			resId = R.drawable.headline2;
		}else if(url.equals("HEADLINE3")){
			resId = R.drawable.headline3;
		}else if(url.equals("HEADLINE4")){
			resId = R.drawable.headline4;
		}else if(url.equals("MOVIE1")){
			resId = R.drawable.movie1;
		}else if(url.equals("MOVIE2")){
			resId = R.drawable.movie2;
		}else if(url.equals("MOVIE3")){
			resId = R.drawable.movie3;
		}else if(url.equals("RATINGS1")){
			resId = R.drawable.rating1;
		}else if(url.equals("RATINGS2")){
			resId = R.drawable.rating2;
		}else if(url.equals("RATINGS3")){
			resId = R.drawable.rating3;
		}else if(url.equals("TELEPLAY1")){
			resId = R.drawable.teleplay1;
		}else if(url.equals("TELEPLAY2")){
			resId = R.drawable.teleplay2;
		}else if(url.equals("TELEPLAY3")){
			resId = R.drawable.teleplay3;
		}
		return PhotoUtil.resId2Bitmap(resId, ctx);
	}
	
	public static Special getSpecial(String id){
		Special special = new Special();

		Photo photo = new Photo();
		photo.setUrl("TELEPLAY1");
		special.setId("special_id");
		special.setPhoto(photo);
		special.setSummary("Static library support version of the framework's Fragment.");
		special.setTotalCount(20);
		for(int i=1; i<=13; i++){
			Article article = new Article();
			if(i%3 == 0)
				article.setType(Type.SPECIAL);
			else if( i%2 == 0)
				article.setType(Type.VIDEO);
			else
				article.setType(Type.TEXT);
			article.setTitle("MOVIE" + i);
			article.setCreateTime(new Date().getTime());
			photo = new Photo();
			photo.setUrl("MOVIE" + ( i%3 + 1) );
			article.setPhoto(photo);
			article.setContent(body);
			special.getArticles().add(article);
		}
		return special;
	}
	
	public static SearchResult getSearchResult(String keyWord){
		SearchResult result = new SearchResult();
		for(int i=1; i<=13; i++){
			Article article = new Article();
			if(i%3 == 0)
				article.setType(Type.SPECIAL);
			else if( i%2 == 0)
				article.setType(Type.VIDEO);
			else
				article.setType(Type.TEXT);
			article.setTitle("MOVIE" + i);
			article.setCreateTime(new Date().getTime());
			Photo photo = new Photo();
			photo.setUrl("MOVIE" + ( i%3 + 1) );
			article.setPhoto(photo);
			article.setContent(body);
			result.getArticles().add(article);
		}
		return result;
	}
	*/
}
