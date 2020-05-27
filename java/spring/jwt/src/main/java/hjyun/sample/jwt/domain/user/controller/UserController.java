package hjyun.sample.jwt.domain.user.controller;

import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.exception.BusinessException;
import hjyun.sample.jwt.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  @ResponseStatus(code = HttpStatus.OK)
  public UserDto me() throws BusinessException {
    return userService.getByUsername(
        (String) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal()
    );
  }

  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public UserDto create(@RequestBody @Valid UserDto userDto) throws BusinessException {
    return userService.create(userDto);
  }

  @PutMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  public UserDto update(@PathVariable long id,
                        @RequestBody @Valid UserDto userDto) throws BusinessException {
    return userService.update(id, userDto);
  }

  @GetMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  public UserDto get(@PathVariable long id, @RequestParam("page") long page)
      throws BusinessException {
    return userService.get(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(code = HttpStatus.OK)
  public void delete(@PathVariable long id) throws BusinessException {
    userService.delete(id);
  }

}
