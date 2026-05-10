package com.company.enroller.persistence;

import java.util.Collection;

import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@Component("participantService")
public class ParticipantService {

    @Autowired
    PasswordEncoder passwordEncoder;

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {

        String hql = "FROM Participant";
		Query query = connector.getSession().createQuery(hql);
		return query.list();

	}

    public Collection<Participant> getAll(String sortBy , String sortOrder, String key) {
        String hql = "FROM Participant WHERE login like :key";

        if ("login".equals(sortBy)) {
            hql += " ORDER BY login";
            if ("DESC".equals(sortOrder)) {hql += " DESC";} else {hql = hql + " ASC";}
        }

        Query query = connector.getSession().createQuery(hql);
        query.setParameter("key", "%" + key + "%");
        return query.list();
    }

    public Participant findByLogin(String login) {
        return (Participant) connector.getSession().get(Participant.class, login);
    }

    public void  add(Participant participant) {
        String hashedPassword = passwordEncoder.encode(participant.getPassword());
        participant.setPassword(hashedPassword);

        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().save(participant);
        transaction.commit();
    }

    public void delete(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().delete(participant);
        transaction.commit();
    }

    public void update(Participant participant) {
        Transaction transaction = connector.getSession().beginTransaction();
        connector.getSession().merge(participant);
        transaction.commit();
    }

}
