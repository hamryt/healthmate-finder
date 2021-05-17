package com.hamryt.helparty.service.mateboard;

import com.hamryt.helparty.dto.board.mateboard.MateBoardDTO;
import com.hamryt.helparty.dto.board.mateboard.request.CreateMateBoardRequest;
import com.hamryt.helparty.dto.board.mateboard.request.UpdateMateBoardRequest;
import com.hamryt.helparty.dto.board.mateboard.response.CreateMateBoardResponse;
import com.hamryt.helparty.dto.board.mateboard.response.GetMateBoardResponse;
import com.hamryt.helparty.dto.board.mateboard.response.UpdateMateBoardResponse;
import com.hamryt.helparty.dto.user.UserDTO;
import com.hamryt.helparty.exception.board.mateboard.DeleteMateBoardFailedException;
import com.hamryt.helparty.exception.board.mateboard.InsertMateBoardFailedException;
import com.hamryt.helparty.exception.board.mateboard.MateBoardNotFoundException;
import com.hamryt.helparty.exception.login.LoginUserDoesNotMatchException;
import com.hamryt.helparty.exception.user.UpdateFailedException;
import com.hamryt.helparty.mapper.MateBoardMapper;
import com.hamryt.helparty.service.user.UserService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MateBoardServiceImpl implements MateBoardService {
    
    private final UserService userService;
    private final MateBoardMapper mateBoardMapper;
    
    @Transactional
    public CreateMateBoardResponse addMateBoard(
        CreateMateBoardRequest createMateBoardRequest,
        Long id
    ) {
        UserDTO user = userService.findUserById(id);
        
        MateBoardDTO mateBoard
            = MateBoardDTO.builder()
            .gym(createMateBoardRequest.getGym())
            .content(createMateBoardRequest.getContent())
            .startTime(createMateBoardRequest.getStartTime())
            .endTime(createMateBoardRequest.getEndTime())
            .userId(user.getId())
            .user(user)
            .build();
        
        if (mateBoardMapper.insertMateBoard(mateBoard) != 1) {
            throw new InsertMateBoardFailedException();
        }
        return CreateMateBoardResponse.of(mateBoard);
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = "mateboards")
    public List<GetMateBoardResponse> getMates(int page, int size) {
        return mateBoardMapper.findMateBoardByPage(page * size, size);
    }
    
    @Transactional(readOnly = true)
    public GetMateBoardResponse getMate(Long id) {
        return Optional.ofNullable(mateBoardMapper.findMateBoardById(id))
            .orElseThrow(() -> new MateBoardNotFoundException(id));
    }
    
    @Transactional
    @CacheEvict(value = "mateboards")
    public void deleteMateBoard(Long boardId, Long sessionId) {
        
        Long boardUserId = mateBoardMapper.findUserIdByMateBoardId(boardId);
        
        if (!boardUserId.equals(sessionId)) {
            throw new LoginUserDoesNotMatchException(sessionId);
        }
        
        if (mateBoardMapper.deleteMateBoardById(boardId) != 1) {
            throw new DeleteMateBoardFailedException();
        }
    }
    
    @Transactional
    @CacheEvict(value = "mateboards")
    public UpdateMateBoardResponse updateMateBoard(
        Long sessionId, Long boardId,
        UpdateMateBoardRequest updateMateBoardRequest
    ) {
        Long userId = mateBoardMapper.findUserIdByMateBoardId(boardId);
        
        if (!sessionId.equals(userId)) {
            throw new LoginUserDoesNotMatchException(sessionId);
        }
        
        UpdateMateBoardResponse updateMateBoardResponse = UpdateMateBoardResponse
            .of(boardId, updateMateBoardRequest);
        
        if (mateBoardMapper.updateMateBoard(updateMateBoardResponse) != 1) {
            throw new UpdateFailedException(boardId);
        }
        
        return updateMateBoardResponse;
    }
    
}
