package com.openmarket.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

public class Tables {

	@Table("User")
	public static class User extends Model {}
	public static User User = new User();
	
	@Table("Address")
	public static class Address extends Model {}
	public static Address Address = new Address();
	
	@Table("Address_view")
	public static class AddressView extends Model {}
	public static AddressView AddressView = new AddressView();
	
	@Table("Country")
	public static class Country extends Model {}
	public static Country Country = new Country();
	
	@Table("Login")
	public static class Login extends Model {}
	public static Login Login = new Login();
		
	@Table("wishlist_item")
	public static class WishlistItem extends Model {}
	public static WishlistItem WishlistItem = new WishlistItem();
	
	@Table("Seller")
	public static class Seller extends Model {}
	public static Seller Seller = new Seller();
	
	@Table("Product")
	public static class Product extends Model {}
	public static Product Product = new Product();
	
	@Table("Product_view")
	public static class ProductView extends Model {}
	public static ProductView ProductView = new ProductView();
	
	@Table("Product_thumbnail_view")
	public static class ProductThumbnailView extends Model {}
	public static ProductThumbnailView ProductThumbnailView = new ProductThumbnailView();
	
	@Table("Product_picture")
	public static class ProductPicture extends Model {}
	public static ProductPicture ProductPicture = new ProductPicture();
	
	@Table("Product_page")
	public static class ProductPage extends Model {}
	public static ProductPage ProductPage = new ProductPage();
	
	@Table("Product_price")
	public static class ProductPrice extends Model {}
	public static ProductPrice ProductPrice = new ProductPrice();
	
	@Table("Product_bullet")
	public static class ProductBullet extends Model {}
	public static ProductBullet ProductBullet= new ProductBullet();
	
	@Table("Shipping")
	public static class Shipping extends Model {}
	public static Shipping Shipping = new Shipping();
	
	@Table("Shipping_cost")
	public static class ShippingCost extends Model {}
	public static ShippingCost ShippingCost = new ShippingCost();
	
	@Table("Shipping_cost_view")
	public static class ShippingCostView extends Model {}
	public static ShippingCostView ShippingCostView = new ShippingCostView();
	
	@Table("Auction")
	public static class Auction extends Model {}
	public static Auction Auction = new Auction();

	@Table("Currency")
	public static class Currency extends Model {}
	public static Currency Currency = new Currency();
	
	@Table("Time_type")
	public static class TimeType extends Model {}
	public static TimeType TimeType = new TimeType();
	
	@Table("Time_span")
	public static class TimeSpan extends Model {}
	public static TimeSpan TimeSpan = new TimeSpan();
	
	@Table("Time_span_view")
	public static class TimeSpanView extends Model {}
	public static TimeSpanView TimeSpanView = new TimeSpanView();
	
	@Table("Category")
	public static class Category extends Model {}
	public static Category Category = new Category();
	
	@Table("category_tree_view")
	public static class CategoryTreeView extends Model {}
	public static CategoryTreeView CategoryTreeView = new CategoryTreeView();
	
	@Table("Review")
	public static class Review extends Model {}
	public static Review Review = new Review();
	
	@Table("Review_view")
	public static class ReviewView extends Model {}
	public static ReviewView ReviewView = new ReviewView();
	
	@Table("Review_vote")
	public static class ReviewVote extends Model {}
	public static ReviewVote ReviewVote = new ReviewVote();
	
	@Table("Review_comment")
	public static class ReviewComment extends Model {}
	public static ReviewComment ReviewComment = new ReviewComment();
	
	@Table("Question")
	public static class Question extends Model {}
	public static Question Question = new Question();
	
	@Table("Question_view")
	public static class QuestionView extends Model {}
	public static QuestionView QuestionView = new QuestionView();
	
	@Table("Question_vote")
	public static class QuestionVote extends Model {}
	public static QuestionVote QuestionVote = new QuestionVote();
	
	@Table("Answer")
	public static class Answer extends Model {}
	public static Answer Answer = new Answer();
	
	@Table("Answer_view")
	public static class AnswerView extends Model {}
	public static AnswerView AnswerView = new AnswerView();
	
	@Table("Answer_vote")
	public static class AnswerVote extends Model {}
	public static AnswerVote AnswerVote = new AnswerVote();
	
	
	@Table("Cart_view")
	public static class CartView extends Model {}
	public static CartView CartView = new CartView();
	
	@Table("Cart_group")
	public static class CartGroup extends Model {}
	public static CartGroup CartGroup = new CartGroup();
	
	@Table("Cart_item")
	public static class CartItem extends Model {}
	public static CartItem CartItem = new CartItem();
	
	@Table("Order_group")
	public static class OrderGroup extends Model {}
	public static OrderGroup OrderGroup = new OrderGroup();
	
	@Table("Order_view")
	public static class OrderView extends Model {}
	public static OrderView OrderView = new OrderView();
	

	@Table("Shipment")
	public static class Shipment extends Model {}
	public static Shipment Shipment = new Shipment();
	
	@Table("Payment")
	public static class Payment extends Model {}
	public static Payment Payment = new Payment();
	
	



	

	
	
	
	

}
