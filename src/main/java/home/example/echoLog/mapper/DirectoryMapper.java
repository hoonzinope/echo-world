package home.example.echoLog.mapper;

import home.example.echoLog.model.Directory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface DirectoryMapper {
    Directory getRootDirectory();
    Optional<Directory> getDirectoryByName(Directory directory);
    Optional<Directory> getDirectoryById(Long dir_id);
    void addDirectory(Directory directory);
    List<Directory> getChildrenDirectories(Long parent_id);
}
