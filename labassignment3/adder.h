#ifndef ADDER_H
#define ADDER_H

using namespace std;

class Adder
{
  public:
    Adder(int input);
    int addOne();
    int addTen();

  private:
    int value;
};

Adder::Adder(int input)
{
  value = input;
}


int Adder::addOne()
{
  return ++value;
}

int Adder::addTen()
{
  return value + 10;
}

#endif
