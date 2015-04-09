package com.openmarket.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

public class Tables {

	@Table("User")
	public static class User extends Model {}
	public static final User USER = new User();
	
	@Table("Address")
	public static class Address extends Model {}
	public static final Address ADDRESS = new Address();
	
	@Table("Address_view")
	public static class AddressView extends Model {}
	public static final AddressView ADDRESS_VIEW = new AddressView();
	
	@Table("Country")
	public static class Country extends Model {}
	public static final Country COUNTRY = new Country();
	
	@Table("Login")
	public static class Login extends Model {}
	public static final Login LOGIN = new Login();
		
	@Table("wishlist_item")
	public static class WishlistItem extends Model {}
	public static final WishlistItem WISHLIST_ITEM = new WishlistItem();
	
	@Table("Seller")
	public static class Seller extends Model {}
	public static final Seller SELLER = new Seller();
	
	@Table("Product")
	public static class Product extends Model {}
	public static final Product PRODUCT = new Product();
	
	@Table("Product_view")
	public static class ProductView extends Model {}
	public static final ProductView PRODUCT_VIEW = new ProductView();
	
	@Table("Product_thumbnail_view")
	public static class ProductThumbnailView extends Model {}
	public static final ProductThumbnailView PRODUCT_THUMBNAIL_VIEW = new ProductThumbnailView();
	
	@Table("Product_picture")
	public static class ProductPicture extends Model {}
	public static final ProductPicture PRODUCT_PICTURE = new ProductPicture();
	
	@Table("Product_page")
	public static class ProductPage extends Model {}
	public static final ProductPage PRODUCT_PAGE = new ProductPage();
	
	@Table("Product_price")
	public static class ProductPrice extends Model {}
	public static final ProductPrice PRODUCT_PRICE = new ProductPrice();
	
	@Table("Product_bullet")
	public static class ProductBullet extends Model {}
	public static final ProductBullet PRODUCT_BULLET= new ProductBullet();
	
	@Table("Shipping")
	public static class Shipping extends Model {}
	public static final Shipping SHIPPING = new Shipping();
	
	@Table("Shipping_cost")
	public static class ShippingCost extends Model {}
	public static final ShippingCost SHIPPING_COST = new ShippingCost();
	
	@Table("Shipping_cost_view")
	public static class ShippingCostView extends Model {}
	public static final ShippingCostView SHIPPING_COST_VIEW = new ShippingCostView();
	
	@Table("Auction")
	public static class Auction extends Model {}
	public static final Auction AUCTION = new Auction();

	@Table("Currency")
	public static class Currency extends Model {}
	public static final Currency CURRENCY = new Currency();
	
	@Table("Time_type")
	public static class TimeType extends Model {}
	public static final TimeType TIME_TYPE = new TimeType();
	
	@Table("Time_span")
	public static class TimeSpan extends Model {}
	public static final TimeSpan TIME_SPAN = new TimeSpan();
	
	@Table("Time_span_view")
	public static class TimeSpanView extends Model {}
	public static final TimeSpanView TIME_SPAN_VIEW = new TimeSpanView();
	
	@Table("Category")
	public static class Category extends Model {}
	public static final Category CATEGORY = new Category();
	
	@Table("category_tree_view")
	public static class CategoryTreeView extends Model {}
	public static final CategoryTreeView CATEGORY_TREE_VIEW = new CategoryTreeView();
	
	@Table("Browse_view")
	public static class BrowseView extends Model {}
	public static final BrowseView BROWSE_VIEW = new BrowseView();
	
	@Table("Review")
	public static class Review extends Model {}
	public static final Review REVIEW = new Review();
	
	@Table("Review_view")
	public static class ReviewView extends Model {}
	public static final ReviewView REVIEW_VIEW = new ReviewView();
	
	@Table("Review_vote")
	public static class ReviewVote extends Model {}
	public static final ReviewVote REVIEW_VOTE = new ReviewVote();
	
	@Table("Review_comment")
	public static class ReviewComment extends Model {}
	public static final ReviewComment REVIEW_COMMENT = new ReviewComment();
	
	@Table("Question")
	public static class Question extends Model {}
	public static final Question QUESTION = new Question();
	
	@Table("Question_view")
	public static class QuestionView extends Model {}
	public static final QuestionView QUESTION_VIEW = new QuestionView();
	
	@Table("Question_vote")
	public static class QuestionVote extends Model {}
	public static final QuestionVote QUESTION_VOTE = new QuestionVote();
	
	@Table("Answer")
	public static class Answer extends Model {}
	public static final Answer ANSWER = new Answer();
	
	@Table("Answer_view")
	public static class AnswerView extends Model {}
	public static final AnswerView ANSWER_VIEW = new AnswerView();
	
	@Table("Answer_vote")
	public static class AnswerVote extends Model {}
	public static final AnswerVote ANSWER_VOTE = new AnswerVote();
	
	
	@Table("Cart_view")
	public static class CartView extends Model {}
	public static final CartView CART_VIEW = new CartView();
	
	@Table("Cart_group")
	public static class CartGroup extends Model {}
	public static final CartGroup CART_GROUP = new CartGroup();
	
	@Table("Cart_item")
	public static class CartItem extends Model {}
	public static final CartItem CART_ITEM = new CartItem();
	
	@Table("Order_group")
	public static class OrderGroup extends Model {}
	public static final OrderGroup ORDER_GROUP = new OrderGroup();
	
	@Table("Order_view")
	public static class OrderView extends Model {}
	public static final OrderView ORDER_VIEW = new OrderView();

	@Table("Shipment")
	public static class Shipment extends Model {}
	public static final Shipment SHIPMENT = new Shipment();
	
	@Table("Payment")
	public static class Payment extends Model {}
	public static final Payment PAYMENT = new Payment();
	
	@Table("Feedback")
	public static class Feedback extends Model {}
	public static final Feedback FEEDBACK = new Feedback();
	
	@Table("Feedback_view")
	public static class FeedbackView extends Model {}
	public static final FeedbackView FEEDBACK_VIEW = new FeedbackView();
	
	



	

	
	
	
	

}
