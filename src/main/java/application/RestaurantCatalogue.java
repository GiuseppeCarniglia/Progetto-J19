package application;

import application.restaurant_exception.RestaurantAlreadyExistingException;
import application.restaurant_exception.RestaurantNotFoundException;
import persistence.OIDCreator;
import persistence.PersistenceFacade;
import persistence.RestaurantsMapper;

import java.sql.SQLException;
import java.util.*;

/**
 * Singleton class
 * A catalogue of the restaurants which are registered in the system
 */
public  class RestaurantCatalogue {
    private static RestaurantCatalogue instance = null;


    /**
     * Create a new RestaurantCatalogue
     * initialize restaurants, HashMap of the restaurants('key':= code of restaurant, 'value':= the restaurant)
     * initialize counter, the counter used to generate the code of the restaurant in the system
     */
    private RestaurantCatalogue(){
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
    public  String addRestaurant(String name, String address,String city, String owner) throws RestaurantAlreadyExistingException,
            SQLException{
        checkExisting(name, address);
        Restaurant r = new Restaurant(name, address, owner,city);
        String restaurantCode = OIDCreator.getInstance().getNewRestaurantCode();
        PersistenceFacade.getInstance().addRestaurant(restaurantCode,r);
        return restaurantCode;
    }


    /**
     * Method which is called when an user wants to see the list of the restaurants in the system
     *
     * @return a map whose keys are the code of the restaurants and the values are the name of the restaurants
     */
    public Map<Integer,String> getAllRestaurantName(){
        Map<Integer, String> rest = new HashMap<>();
        for(Map.Entry<String, Restaurant> e : getAllRestaurants().entrySet()){
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
    public LinkedHashMap<String, String>  getMenuInfo(String restaurantCode)throws SQLException{
        return getRestaurant(restaurantCode).getMenuInfo();
    }

    /**
     * Method which is called by 'addRestaurant' to check if a new restaurant which is in registration step,
     * is already in the system
     *
     * @param name of the restaurant to register
     * @param address of the restaurant to register
     */
    private void checkExisting(String name, String address){
        Map<String, Restaurant> restaurantsCopy = getAllRestaurants();
        if(restaurantsCopy.isEmpty())
            return;
        for (String code : restaurantsCopy.keySet()) {
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
    private boolean checkInfo(String code,String name, String address){
        Map<String, Restaurant> restaurantsCopy = getAllRestaurants();
        return restaurantsCopy.get(code).getName().equals(name) &&
                restaurantsCopy.get(code).getAddress().equals(address);
    }

    /**
     * Method which is called to show to restaurant's owner the list of his restaurants
     *
     * @param owner of the restaurants (username)
     * @return a map whose keys are the code of the of the restaurants and the values are the name of the restaurants
     */
    public Map<Integer, String> myRestaurant(String owner){
        HashMap<Integer,String> myRest = new HashMap<>();
        for(Map.Entry<String, Restaurant> restaurant:getAllRestaurants().entrySet()) {
            if (restaurant.getValue().getOwner().equals(owner))
                myRest.put(Integer.parseInt(restaurant.getKey()), restaurant.getValue().getName());
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
     * @param dishName the name of the dish
     * @param price the price of the dish
     */
    public void addMenuEntry(String restaurantCode,String dishType,String dishName, double price)throws SQLException{
       Restaurant r = getRestaurant(restaurantCode);
       r.checkMenuEntryExistence(dishName);
       MenuEntry me = r.addMenuEntryToMenu(dishType,dishName,price,OIDCreator.getInstance().getNewMenuEntryCode()
               ,restaurantCode);
       PersistenceFacade.getInstance().addMenuEntry(me);

    }

    public ArrayList<String> getMenuCode(String restCode)throws SQLException{
        return getRestaurant(restCode).getMenuCode();
    }

    public MenuEntry getDish(String restCode, String dishCode)throws SQLException{
        return getRestaurant(restCode).getDish(dishCode);
    }

    public HashMap<String,String> getRestaurantOverview(String restCode)throws SQLException{
        return getRestaurant(restCode).getOverview();
    }
    public String getRestaurantName(String restaurantCode)throws SQLException{
        return getRestaurant(restaurantCode).getName();
    }
    public String getRestaurantAddress(String restaurantCode)throws SQLException{
        return getRestaurant(restaurantCode).getCityAddress();
    }

    public void setRestaurantOverview(String restaurantCode,RestaurantOverview overview)throws SQLException{
        getRestaurant(restaurantCode).setOverview(overview);
    }

    public double getRestaurantMeanVote(String restaurantCode)throws SQLException{
        return  getRestaurant(restaurantCode).getMeanVote();
    }

    private Restaurant getRestaurant(String restaurantCode) throws SQLException{

        return (Restaurant) PersistenceFacade.getInstance().get(restaurantCode, RestaurantsMapper.class);

    }

    private Map<String, Restaurant> getAllRestaurants(){
        return PersistenceFacade.getInstance().getAllRestaurants();
    }

    public LinkedHashMap<String, List<String>> restaurantMenuToString(String restaurantCode) throws SQLException {
        return getRestaurant(restaurantCode).menuToString();
    }
}
    
