package persistence;

import application.Critique;
import application.MenuEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper of the table "critique_dish".
 * Table in which there are the codes of the critiques and
 * the dishes who are valuated in them with their grade.
 */
public class DishCritiquesMapper extends AbstractPersistenceMapper {
    private MenuEntryMapper menuEntryMapper;

    public DishCritiquesMapper(MenuEntryMapper menuEntryMapper) throws SQLException {
        super("critique_dish");
        this.menuEntryMapper = menuEntryMapper;
    }

    @Override
    protected Object getObjectFromTable(String OID) throws SQLException {
        return null;
    }

    @Override
    protected Object getObjectFromCache(String OID) {
        return null;
    }

    @Override
    protected void updateCache(String OID, Object obj) {

    }

    @Override
    public void put(String OID, Object obj) {
        Critique c = (Critique)obj;
        try{
            for (Map.Entry<MenuEntry,Double> temp:c.getDishes().entrySet()) {
                PreparedStatement pstm = conn.prepareStatement("INSERT INTO "+tableName+" VALUES(?,?,?)");
                pstm.setString(1,OID);
                pstm.setString(2,Integer.toString(temp.getKey().getCod()));
                pstm.setDouble(3,temp.getValue());
                pstm.execute();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method called by CritiquesMapper when the system is set up,
     * in order to instance each critique with its MenuEntry
     * @param critiqueCode
     * @return the dishes of the critique , each one matched with its grade
     */
    protected HashMap<MenuEntry,Double> getDishesGrades(String critiqueCode){
        HashMap<MenuEntry, Double> gradeDish = new HashMap<>();
        try{
            PreparedStatement pstm = conn.prepareStatement("SELECT DISH_CODE,VOTO_DISH FROM "+tableName+" WHERE CRITIQUE_CODE = ?" );
            pstm.setInt(1, Integer.parseInt(critiqueCode));
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){
                gradeDish.put((MenuEntry) menuEntryMapper.get(Integer.toString(rs.getInt(1))),
                        Double.parseDouble(rs.getString(2)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gradeDish;
    }
}
