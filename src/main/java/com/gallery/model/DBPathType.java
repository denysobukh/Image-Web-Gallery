package com.gallery.model;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

/**
 * DBPathType class
 *
 * @author Dennis Obukhov
 * @date 2019-04-12 14:34 [Friday]
 */
public class DBPathType implements UserType {
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Class returnedClass() {
        return Path.class;
    }

    @Override
    public boolean equals(Object o, Object o1) throws HibernateException {
        if (o == null || o1 == null) return false;
        if (o == o1) return true;
        if (o.getClass() != o1.getClass()) return false;
        if (!(o instanceof Path) || !(o1 instanceof Path)) return false;
        return o.equals(o1);
    }

    @Override
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        String pathStr = resultSet.getString(strings[0]);
        if (resultSet.wasNull()) return null;
        return Paths.get(pathStr);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (Objects.isNull(o)) {
            preparedStatement.setNull(i, Types.VARCHAR);
        } else {
            Path path = (Path) o;
            preparedStatement.setString(i, path.toString());
        }
    }

    @Override
    public Object deepCopy(Object o) throws HibernateException {
        return o;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(Object o) throws HibernateException {
        return o.toString();
    }

    @Override
    public Object assemble(Serializable serializable, Object o) throws HibernateException {
        return Paths.get(o.toString());
    }

    @Override
    public Object replace(Object o, Object o1, Object o2) throws HibernateException {
        return o;
    }
}
