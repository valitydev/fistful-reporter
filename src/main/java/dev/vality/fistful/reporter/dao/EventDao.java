package dev.vality.fistful.reporter.dao;

import dev.vality.dao.GenericDao;

import java.util.Optional;

public interface EventDao extends GenericDao {

    Optional<Long> getLastEventId();

}
