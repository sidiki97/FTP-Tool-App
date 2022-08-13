package com.vault.ftp.ftptool.Database;


import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ParentDirectoryRepo extends CrudRepository<ParentDirectory, Long> {

    List<ParentDirectory> findByParent(String parent);


}
