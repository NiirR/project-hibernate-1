package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    SessionFactory sessionFactory;
    public PlayerRepositoryDB() {
        Properties properties = new Properties();

        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "12345");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
    try(Session session = sessionFactory.openSession()){
        NativeQuery<Player> query = session.createNativeQuery("SELECT * FROM rpg.player", Player.class);
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        return query.list();
    }catch (Exception e){
        throw new RuntimeException(e);
    }
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()){
            Query<Long> query = session.createNativeQuery("SELECT COUNT(*) FROM rpg.player", Long.class);
            return query.list().size();
        }

    }

    @Override
    public Player save(Player player) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.save(player);
            session.getTransaction().commit();
            return player;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Player update(Player player) {
       try(Session session = sessionFactory.openSession()){
           session.beginTransaction();
           session.update(player);
           session.getTransaction().commit();
           return player;
       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }

    @Override
    public Optional<Player> findById(long id) {
       try(Session session = sessionFactory.openSession()){
           Player player = session.get(Player.class, id);
           return Optional.ofNullable(player);
       }catch (Exception e){
           throw new RuntimeException(e);
       }
    }

    @Override
    public void delete(Player player) {
    try(Session session = sessionFactory.openSession()){
        session.beginTransaction();
        session.delete(player);
        session.getTransaction().commit();
    }catch (Exception e){
        throw new RuntimeException(e);
    }
    }




    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}