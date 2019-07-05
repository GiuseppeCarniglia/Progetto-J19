package application;

import application.restaurant_exception.RestaurantAlreadyExistingException;
import application.restaurant_exception.RestaurantNotFoundException;
import persistence.PersistenceFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Singleton class
 * A catalogue of the restaurants which are registered in the system
 */
public  class RestaurantCatalogue {
    private static RestaurantCatalogue instance = null;
    //private  Map<Integer, Restaurant> restaurants;
    private  int counter;

    /**
     * Create a new RestaurantCatalogue
     * initialize restaurants, HashMap of the restaurants('key':= code of restaurant, 'value':= the restaurant)
     * initialize counter, the counter used to generate the code of the restaurant in the system
     */
    private RestaurantCatalogue(){
        //this.restaurants = new HashMap<>();
        this.counter = 0;
    }

    /**
     * 'Pattern Singleton Implementation'
     *
     * If class has not been already created it instantiates the class and returns the instance
     * @return instance(RestaurantCatalogue)
     */
    public static synchronized RestaurantCatalogue getInstance(){
        if(instance == null)
            instance = new RestaurantCatalogue();
        return instance;
    }

    /**
     * Add a restaurant in the catalogue system
     * @param name of the restaurant
     * @param address of the restaurant
     * @param owner of the restaurant(username)
     * @return the counter of the class used to create the code of the new restaurant
     * @throws RestaurantAlreadyExistingException
     */
    public  int addRestaurant(String name, String address, String owner) throws RestaurantAlreadyExistingException{
        checkExisting(name, address);
        Restaurant r = new Restaurant(name, address, owner);
        restaurants.put(++counter, r);
        return counter;
    }

    /**
     * Add a new menu to the restaurant selected by the code
     * @param menu the menu of the restaurant
     * @param key the code of the restaurant
     */
    public  void addMenu(HashMap<DishType,ArrayList<MenuEntry>> menu, int key){
        restaurants.get(key).addMenu(menu);
    }


    public  void printMenu(int key){
       restaurants.get(key).printMenu();
    }

    /**
     * Method which is called when an user wants to see the list of the restaurants in the system
     *
     * @return a map whose keys are the code of the restaurants and the values are the name of the restaurants
     */
    public Map<Integer,String> getAllRestaurantName(){
        Map<Integer, String> rest = new HashMap<>();
        for(Map.Entry<String, Restaurant> e : PersistenceFacade.getInstance().getAllRestaurants().entrySet()){
            rest.put(Integer.parseInt(e.getKey()),e.getValue().getName());
        }
        return rest;
    }

    /**
     * Method which is called when a critic write a critique of a restaurant,
     * in order to show the menu of the restaurant
     *
     * @param restaurantCode the code oh the restaurant
     * @return a map whose keys are the code of the of the dishes of the restaurant and the values are the name of the dishes
     */
    public LinkedHashMap<Integer, String>  getMenuInfo(int restaurantCode){
        return this.restaurants.get(restaurantCode).getMenuInfo();
    }

    /**
     * Method which is called by 'addRestaurant' to check if a new restaurant which is in registration step,
     * is already in the system
     *
     * @param name of the restaurant to register
     * @param address of the restaurant to register
     */
    private void checkExisting(String name, String address){
        if(this.restaurants.isEmpty())
            return;
        for (int code : this.restaurants.keySet()) {
            if (checkInfo(code, name, address))
                throw new RestaurantAlreadyExistingException("Il ristorante è già presente nel sistema !");
        }
    }

    /**
     * Called by 'checkExisting' to verify if the name and address input are the same of the ones of the restaurant
     * identified by the code
     *
     * @param code of the restaurant in the system
     * @param name of the restaurant in registration step
     * @param address of the restaurant in registration step
     * @return a boolean (true if name or address are the same of the restaurant in the system)
     */
    private boolean checkInfo(int code,String name, String address){
        return this.restaurants.get(code).getName().equals(name) &&
                this.restaurants.get(code).getAddress().equals(address);
    }

    /**
     * Method which is called to show to restaurant's owner the list of his restaurants
     *
     * @param owner of the restaurants (username)
     * @return a map whose keys are the code of the of the restaurants and the values are the name of the restaurants
     */
    public Map<Integer, String> myRestaurant(String owner){
        HashMap<Integer,String> myRest = new HashMap<>();
        for(Map.Entry<Integer, Restaurant> restaurant: this.restaurants.entrySet()) {
            if (restaurant.getValue().getOwner().equals(owner))
                myRest.put(restaurant.getKey(), restaurant.getValue().getName());
        }
        if(myRest.isEmpty())
            throw new RestaurantNotFoundException("Nessun ristorante in tuo possesso");
        return myRest;
    }

    /**
     * Method called when the restaurant's owner adds a dish to the menu of the restaurant.
     * It adds a new dish to a menu of a restaurant
     *
     * @param restaurantCode the code of the restaurant
     * @param dishType the type of the dish
     * @param dish the name of the dish
     * @param price the price of the dish
     */
    public void addMenuEntry(int restaurantCode,String dishType,String dish, double price) {
        this.restaurants.get(restaurantCode).addMenuEntry(dishType,dish,price);
    }

    public ArrayList<Integer> getMenuCode(int restCode){
        return this.restaurants.get(restCode).getMenuCode();
    }

    public MenuEntry getDish(int restCod, int dishCod){
        return this.restaurants.get(restCod).getDish(dishCod);
    }

    public HashMap<String,String> getRestaurantOverview(int restCod){
        return this.restaurants.get(restCod).getOverview();
    }
    public String getRestaurantName(int restaurantCode){
        return this.restaurants.get(restaurantCode).getName();
    }
    public String getRestaurantAddress(int restaurantCode){
        return this.restaurants.get(restaurantCode).getAddress();
    }

    public void setRestaurantOverview(int restaurantCode,RestaurantOverview overview){
        this.restaurants.get(restaurantCode).setOverview(overview);
    }

    public double getRestaurantMeanVote(int restaurantCode){
        return this.restaurants.get(restaurantCode).getMeanVote();
    }

}
    
