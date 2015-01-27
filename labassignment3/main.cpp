#include "adder.h"
#include <iostream>
#include <cassert>

using namespace std;

int main() 
{
  Adder* add1 = new Adder(5);
  assert (add1->addOne() == 6);
  Adder* add2 = new Adder(10);
  assert (add2->addTen() == 20);
  cout << endl << "Success!" << endl;
  return 0;
}
