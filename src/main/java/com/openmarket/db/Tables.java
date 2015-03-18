package com.openmarket.db;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

public class Tables {

	@Table("User")
	public static class User extends Model {}
	
	@Table("Address")
	public static class Address extends Model {}
	
	@Table("Country")
	public static class Country extends Model {}
	
	@Table("Login")
	public static class Login extends Model {}
	
	@Table("Wishlist")
	public static class Wishlist extends Model {}
	
	@Table("wishlist_item")
	public static class WishlistItem extends Model {}
	
	@Table("Seller")
	public static class Seller extends Model {}
	
	@Table("Product")
	public static class Product extends Model {}
	
	@Table("Product_view")
	public static class ProductView extends Model {}
	
	@Table("Product_thumbnail_view")
	public static class ProductThumbnailView extends Model {}
	
	@Table("Product_picture")
	public static class ProductPicture extends Model {}
	
	@Table("Product_page")
	public static class ProductPage extends Model {}
	
	@Table("Product_price")
	public static class ProductPrice extends Model {}
	
	@Table("Product_bullet")
	public static class ProductBullet extends Model {}
	
	@Table("Shipping")
	public static class Shipping extends Model {}
	
	@Table("Shipping_cost")
	public static class ShippingCost extends Model {}
	
	@Table("Shipping_cost_view")
	public static class ShippingCostView extends Model {}
	
	@Table("Auction")
	public static class Auction extends Model {}

	@Table("Currency")
	public static class Currency extends Model {}
	
	@Table("Time_type")
	public static class TimeType extends Model {}
	
	@Table("Time_span")
	public static class TimeSpan extends Model {}
	
	@Table("Time_span_view")
	public static class TimeSpanView extends Model {}
	
	@Table("Category")
	public static class Category extends Model {}
	
	@Table("category_tree_view")
	public static class CategoryTreeView extends Model {}
	
	@Table("Review")
	public static class Review extends Model {}
	
	@Table("Review_view")
	public static class ReviewView extends Model {}
	
	@Table("Review_vote")
	public static class ReviewVote extends Model {}
	
	@Table("Review_comment")
	public static class ReviewComment extends Model {}
	
	@Table("Question")
	public static class Question extends Model {}
	
	@Table("Question_view")
	public static class QuestionView extends Model {}
	
	@Table("Question_vote")
	public static class QuestionVote extends Model {}
	
	@Table("Answer")
	public static class Answer extends Model {}
	
	@Table("Answer_vote")
	public static class AnswerVote extends Model {}
	
	
	
	

}
