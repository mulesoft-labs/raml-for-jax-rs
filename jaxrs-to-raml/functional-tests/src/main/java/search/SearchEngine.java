package search;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;


@Path("/rest")
@Produces("application/json")
public class SearchEngine {

	@GET
	@Path("/search/docs.json")
	@Produces("application/json; charset=UTF-8")
	public SearchResult search(@QueryParam(value = "category") String category,
			@QueryParam(value = "place") String place,
			@QueryParam(value = "keywords") String keywords,
			@QueryParam(value = "count") Integer count,
			@QueryParam(value = "dates") String dates,
			@QueryParam(value = "offset") Integer offset) {
		SearchResult searchResult = new SearchResult();
		if (offset == null) {
			offset = 0;
		}
		if (count == null) {
			count = 20;
		}
		CategoryInfo schema = schema();
		
		CategoryInfo inf=schema.find(category);
		if (category!=null&&inf==null){
			searchResult.setErrror(1,"No such category");
			return searchResult;
		}
		searchResult.setOffset(offset);
		searchResult.setCount(count);
		searchResult.setTotalCount(100);
//		for (IDocument d : search) {
//			Document document = new Document();
//			document.title = d.getTitle();
//			document.id = d.getId();
//			document.url = d.getUrl();
//			document.abstractText = d.getPlainTextAbstract();
//			document.richText = d.getPlainTextAbstract();
//			document.imageLink = d.getImageLink();
//			if(place!=null){
//				document.addLocationRef(new LocationRef(place,new UID().toString()));
//			}
//			if (inf!=null){
//				document.addCategoryRef(new CategoryRef(inf.getId(),inf.getName()));
//			}			
//			searchResult.getDocuments().add(document);
//		}
		return searchResult;
	}

	@GET
	@Path("/schema.json")
	@Produces("application/json; charset=UTF-8")
	public CategoryInfo schema() {
		CategoryInfo root = new CategoryInfo();
		
		return root;
	}

//	private void fill(CategoryInfo categoryInfo, UICategory z) {
//		categoryInfo.setId(z.getId());
//		categoryInfo.setDescription(z.getDescription());
//		categoryInfo.setName(z.getTitle());
//		ArrayList<UICategory> elements = z.getChildren();
//		for (UICategory za:elements){
//			CategoryInfo ca = new CategoryInfo();
//			fill(ca,za);
//			categoryInfo.getChildren().add(ca);
//		}
//	}

	@GET
	@Path("/search/doc.json")
	@Produces("application/json; charset=UTF-8")
	public Document search(@QueryParam(value = "id") String id) {
		//IDocument d = SearchSystem.document(id);
		Document document = new Document();
//		document.title = d.getTitle();
//		document.id = d.getId();
//		document.url = d.getUrl();
//		document.abstractText = d.getPlainTextAbstract();
//		document.richText = d.getPlainTextAbstract();
//		document.imageLink = d.getImageLink();
		return document;
	}

	@GET
	@Path("/search/update.json")
	public void update(@QueryParam(value = "url") String url,
			@QueryParam(value = "title") String title,
			@QueryParam(value = "image") String image,
			@QueryParam(value = "text") String text,
			@QueryParam(value = "time") String time,
			@QueryParam(value = "place") String place,
			@QueryParam(value = "category") String category,
			@QueryParam(value = "user") boolean user,
			@QueryParam(value = "dateCreated") Date dateCreated,
			@QueryParam(value = "dateEdited") Date dateEdited) {

	}

	@GET
	@Path("/search/viewUpdate.json")
	public void updateViewData(@QueryParam(value = "url") String url,
			@QueryParam(value = "viewCount") int viewCount,
			@QueryParam(value = "likeCount") int likeCount,
			@QueryParam(value = "favCount") int favCount,
			@QueryParam(value = "notLike") int notLike) {
		SearchResult searchResult = new SearchResult();
		Document document = new Document();
		document.title = "AA";
		searchResult.getDocuments().add(document);
		return;
	}

	@GET
	@Path("/search/cats.json")
	public void updateCategories(@QueryParam("url") String url,
			@QueryParam("cats") String cats) {
		SearchResult searchResult = new SearchResult();
		Document document = new Document();
		document.title = "AA";
		searchResult.getDocuments().add(document);
		return;
	}
}