import { TestBed } from '@angular/core/testing';
import { Navbar } from './navbar';

describe('Navbar (standalone)', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Navbar],
    }).compileComponents();
  });

  it('should create the Navbar component', () => {
    const fixture = TestBed.createComponent(Navbar);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });

  it('should toggle theme without throwing', () => {
    const fixture = TestBed.createComponent(Navbar);
    const component = fixture.componentInstance;
    expect(() => component.toggleTheme()).not.toThrow();
  });

  it('should toggle sidenav without throwing', () => {
    const fixture = TestBed.createComponent(Navbar);
    const component = fixture.componentInstance;
    const initial = component.isSidenavOpen;
    component.toggleSidenav();
    expect(component.isSidenavOpen).toBe(!initial);
  });
});
